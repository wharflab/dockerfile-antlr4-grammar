# Dockerfile ANTLR4 Grammar

This project provides a comprehensive ANTLR4 grammar for Dockerfiles.

## Features

- **Dockerfile Support:**
  - Lexer Modes for instructions/arguments.
  - Robust shell command parsing.
  - Recursive `ONBUILD` and `HEALTHCHECK CMD`.
  - Support for nested blocks, lists (block and flow), and key-value pairs.
- **Form Support:** Handles both shell form and exec form (`[...]`) for instructions.
- **Builder Flags:** Captures decoded leading flags on `FROM`, `RUN`, `ADD`,
  `COPY`, and `HEALTHCHECK`.
- **Parser Directives:** Honors top-of-file `# escape=\` and ``# escape=` `` directives.
- **Line Continuations:** Uses the effective `\` or backtick escape character for multi-line instructions.
- **Comments:** Supports single-line comments starting with `#`.

## Files

- `grammars/DockerfileLexer.g4`, `grammars/DockerfileParser.g4`: Dockerfile grammar.
- `tests/`: Dockerfile fixtures used as test cases, including several large official images.
- `scripts/run_tests.sh`: Generates the parser, compiles it, and parses every fixture.

## How to use

Requirements: a JDK (`java` + `javac`) and Maven. Everything else is handled by the script.

```bash
scripts/run_tests.sh          # generate, compile, and parse all fixtures
scripts/run_tests.sh -t       # same, but also print each parse tree
```

The script downloads the pinned ANTLR version from Maven Central into `.antlr/`
(git-ignored) and verifies its SHA-256 before use. That download uses an isolated,
in-repo Maven repository rather than the shared `~/.m2`, so results do not depend on
what a given machine happens to have cached. The first run fetches the tool; later
runs need no network.

To use a different ANTLR version, set `ANTLR_VERSION` (note that only the pinned
default is checksum-verified):

```bash
ANTLR_VERSION=4.13.1 scripts/run_tests.sh
```

A fixture passes when it parses with no lexer or parser diagnostics. `TestRig` exits `0`
even on syntax errors, so the script treats any error output as a failure and exits
non-zero, reporting which fixtures failed.

The same script runs in CI on every pull request (`.github/workflows/tests.yml`).

## Compare with BuildKit

`scripts/compare_ast.sh` compares this grammar with the official parser from
BuildKit's `frontend/dockerfile/parser` Go package:

```bash
scripts/compare_ast.sh tests/arguments.dockerfile
scripts/compare_ast.sh --keep .ast-diff tests/*.dockerfile
```

The adapters project both parser-specific trees into a common JSON document:
instruction name, argument kind and values, builder flags, nested instructions,
heredocs, effective escape token, and source lines. The projection is
deliberately literal:

- BuildKit values come directly from `parser.Node` fields.
- ANTLR values come directly from emitted tokens and parse-tree structure.
- Neither adapter rereads source text or normalizes whitespace, continuations,
  quoting, escapes, or argument grouping.

Comments and parser warnings are not part of the comparison. A unified JSON
diff is printed for every mismatch; `--keep` retains both projections and their
diagnostics.

The command exits `0` when all ASTs match or both parsers reject an input, `1`
for an acceptance or AST difference, and `2` for a tooling failure. In addition
to the Java and Maven requirements above, it requires Go at the version declared
in `tools/buildkit-ast/go.mod`. The BuildKit dependency is pinned there so
comparisons are reproducible.

CI runs the strict comparison over every fixture. Any acceptance or AST
difference fails the workflow. The current honest baseline is 11 AST
differences across 21 fixtures. The remaining differences are JSON string
decoding and the source location of a nested `ONBUILD` instruction. The workflow
is intentionally red until those differences are fixed in the grammar.

`tests/arguments.dockerfile` and `tests/argument-edges.dockerfile` cover
BuildKit's command-specific argument nodes: opaque shell text, raw
whitespace-delimited lists, quote- and escape-aware words, `ENV`/`LABEL`
key-value triples, and `HEALTHCHECK` type plus command values. CI compares this
matching subset separately before running the intentionally strict full corpus.

`tests/builder-flag-values.dockerfile` and
`tests/builder-flag-values-backtick.dockerfile` cover quote removal, escaped
characters, continuations, empty-quote terminators, and the boundary where
ordinary argument text must remain raw. Dedicated lexer modes produce the flag
values before the adapter reads their parser contexts.

The escape-directive fixtures cover directive scope, backtick continuations,
escaped argument text, and builder flags. The effective escape token comes from
the lexer and is included in the projected AST.

`testdata/ast-parity/heredoc.dockerfile` is an intentional mismatch corpus:
BuildKit accepts it and attaches the heredoc to the `RUN` node, while the
current ANTLR grammar rejects the heredoc body.

## Grammar Design

The lexer starts in `DEFAULT_MODE` for the parser-directive preamble, then uses
separate backslash and backtick body and argument modes. Once an instruction
keyword is found, its argument mode consumes the rest of the logical line. This
keeps words such as `RUN` or `CMD` inside shell commands as argument text while
letting the selected escape character control continuations.

Parser rules then mirror BuildKit's command dispatch. Shell-form commands retain
one argument value with internal whitespace, list-form commands split on raw
whitespace, and `ARG`/`ENV`/`LABEL` use quote- and escape-aware words. These
groupings are visible in the parse tree, so the parity adapter does not need to
reread or normalize source text.

Argument preamble modes recognize only leading builder flags. Quotes, escapes,
and continuations are removed while those modes emit `builder_flag` content;
the first ordinary argument or flag terminator switches to raw argument modes.

`HEALTHCHECK` and `ONBUILD` are handled specially to allow recursive instruction recognition (e.g., `HEALTHCHECK CMD ...`).

## License

BSD 3-Clause. See [LICENSE](LICENSE).
