package main

import (
	"encoding/json"
	"fmt"
	"os"

	"github.com/moby/buildkit/frontend/dockerfile/parser"
)

type document struct {
	Escape       string        `json:"escape"`
	Instructions []instruction `json:"instructions"`
}

type instruction struct {
	Command      string        `json:"command"`
	ArgumentKind string        `json:"argumentKind"`
	Arguments    []string      `json:"arguments"`
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

// This adapter copies parser.Node fields without reparsing Original or
// normalizing values. The comparison must expose differences, not repair them.
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
			toInstruction(child),
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

func toInstruction(node *parser.Node) instruction {
	output := instruction{
		Command:   node.Value,
		Arguments: []string{},
		Flags:     append([]string{}, node.Flags...),
		Children:  []instruction{},
		Heredocs:  []heredoc{},
		Location:  location{StartLine: node.StartLine, EndLine: node.EndLine},
	}

	if node.Attributes["json"] {
		output.ArgumentKind = "json"
	} else {
		output.ArgumentKind = "text"
	}

	for _, child := range node.Children {
		output.Children = append(output.Children, toInstruction(child))
	}
	for next := node.Next; next != nil; next = next.Next {
		if len(next.Children) > 0 {
			for _, child := range next.Children {
				output.Children = append(output.Children, toInstruction(child))
			}
		} else {
			output.Arguments = append(output.Arguments, next.Value)
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
