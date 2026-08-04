package main

import (
	"encoding/json"
	"fmt"
	"os"
	"strings"
	"unicode"

	"github.com/moby/buildkit/frontend/dockerfile/parser"
)

type document struct {
	Escape       string        `json:"escape"`
	Instructions []instruction `json:"instructions"`
}

type instruction struct {
	Command      string        `json:"command"`
	ArgumentKind string        `json:"argumentKind"`
	Arguments    any           `json:"arguments"`
	Flags        []string      `json:"flags"`
	Children     []instruction `json:"children"`
	Heredocs     []heredoc     `json:"heredocs"`
	Location     location      `json:"location"`
}

type heredoc struct {
	Name           string `json:"name"`
	FileDescriptor uint   `json:"fileDescriptor"`
	Expand         bool   `json:"expand"`
	Chomp          bool   `json:"chomp"`
	Content        string `json:"content"`
}

type location struct {
	StartLine int `json:"startLine"`
	EndLine   int `json:"endLine"`
}

func main() {
	if len(os.Args) != 2 {
		fmt.Fprintln(os.Stderr, "usage: buildkit-ast DOCKERFILE")
		os.Exit(2)
	}

	input, err := os.Open(os.Args[1]) //nolint:gosec // The user explicitly supplies this path.
	if err != nil {
		fmt.Fprintf(os.Stderr, "%s: %v\n", os.Args[1], err)
		os.Exit(2)
	}
	defer input.Close()

	result, err := parser.Parse(input)
	if err != nil {
		fmt.Fprintf(os.Stderr, "%s: %v\n", os.Args[1], err)
		os.Exit(1)
	}
	for _, warning := range result.Warnings {
		fmt.Fprintf(os.Stderr, "%s: warning: %s\n", os.Args[1], warning.Short)
	}

	escape := string(result.EscapeToken)
	output := document{
		Escape:       escape,
		Instructions: make([]instruction, 0, len(result.AST.Children)),
	}
	for _, child := range result.AST.Children {
		output.Instructions = append(
			output.Instructions,
			toInstruction(child, result.EscapeToken, child.StartLine, child.EndLine),
		)
	}

	encoder := json.NewEncoder(os.Stdout)
	encoder.SetEscapeHTML(false)
	encoder.SetIndent("", "  ")
	if err := encoder.Encode(output); err != nil {
		fmt.Fprintf(os.Stderr, "encode AST: %v\n", err)
		os.Exit(2)
	}
}

func toInstruction(node *parser.Node, escape rune, fallbackStart, fallbackEnd int) instruction {
	startLine := node.StartLine
	endLine := node.EndLine
	if startLine <= 0 {
		startLine = fallbackStart
	}
	if endLine <= 0 {
		endLine = fallbackEnd
	}

	output := instruction{
		Command:  strings.ToLower(node.Value),
		Flags:    append([]string{}, node.Flags...),
		Children: []instruction{},
		Heredocs: []heredoc{},
		Location: location{StartLine: startLine, EndLine: endLine},
	}

	if node.Attributes["json"] {
		output.ArgumentKind = "json"
		values := []string{}
		for next := node.Next; next != nil; next = next.Next {
			values = append(values, next.Value)
		}
		output.Arguments = values
	} else {
		output.ArgumentKind = "text"
		output.Arguments = normalizeText(argumentText(node, escape), escape)
	}

	if output.Command == "onbuild" {
		for next := node.Next; next != nil; next = next.Next {
			for _, child := range next.Children {
				output.Children = append(
					output.Children,
					toInstruction(child, escape, startLine, endLine),
				)
			}
		}
	}

	for _, value := range node.Heredocs {
		output.Heredocs = append(output.Heredocs, heredoc{
			Name:           value.Name,
			FileDescriptor: value.FileDescriptor,
			Expand:         value.Expand,
			Chomp:          value.Chomp,
			Content:        value.Content,
		})
	}

	return output
}

func argumentText(node *parser.Node, escape rune) string {
	line := strings.TrimSpace(node.Original)
	for index, current := range line {
		if unicode.IsSpace(current) {
			line = strings.TrimLeftFunc(line[index:], unicode.IsSpace)
			break
		}
		if index == len(line)-1 {
			return ""
		}
	}

	for range node.Flags {
		line = consumeWord(line, escape)
	}
	if supportsBuilderFlags(node.Value) {
		line = consumeBuilderFlagTerminator(line, escape)
	}
	return strings.TrimSpace(line)
}

func supportsBuilderFlags(command string) bool {
	switch strings.ToLower(command) {
	case "from", "run", "add", "copy", "healthcheck":
		return true
	default:
		return false
	}
}

func consumeBuilderFlagTerminator(value string, escape rune) string {
	value = strings.TrimLeftFunc(value, unicode.IsSpace)
	if !strings.HasPrefix(value, "--") {
		return value
	}
	if word, remaining := splitWord(value, escape); word == "--" {
		return remaining
	}
	return value
}

func consumeWord(value string, escape rune) string {
	_, remaining := splitWord(value, escape)
	return remaining
}

func splitWord(value string, escape rune) (string, string) {
	value = strings.TrimLeftFunc(value, unicode.IsSpace)
	var word strings.Builder
	quote := rune(0)
	escaped := false

	for index, current := range value {
		if escaped {
			word.WriteRune(current)
			escaped = false
			continue
		}
		if current == escape {
			escaped = true
			continue
		}
		if quote != 0 {
			if current == quote {
				quote = 0
			} else {
				word.WriteRune(current)
			}
			continue
		}
		if current == '\'' || current == '"' {
			quote = current
			continue
		}
		if unicode.IsSpace(current) {
			return word.String(), strings.TrimLeftFunc(value[index:], unicode.IsSpace)
		}
		word.WriteRune(current)
	}

	return word.String(), ""
}

func normalizeText(value string, escape rune) string {
	var result strings.Builder
	quote := rune(0)
	escaped := false
	pendingSpace := false

	for _, current := range value {
		if escaped {
			result.WriteRune(current)
			escaped = false
			continue
		}
		if current == escape && quote != '\'' {
			if pendingSpace && result.Len() > 0 {
				result.WriteByte(' ')
				pendingSpace = false
			}
			result.WriteRune(current)
			escaped = true
			continue
		}
		if quote != 0 {
			result.WriteRune(current)
			if current == quote {
				quote = 0
			}
			continue
		}
		if current == '\'' || current == '"' {
			if pendingSpace && result.Len() > 0 {
				result.WriteByte(' ')
				pendingSpace = false
			}
			quote = current
			result.WriteRune(current)
			continue
		}
		if unicode.IsSpace(current) {
			pendingSpace = result.Len() > 0
			continue
		}
		if pendingSpace {
			result.WriteByte(' ')
			pendingSpace = false
		}
		result.WriteRune(current)
	}

	return result.String()
}
