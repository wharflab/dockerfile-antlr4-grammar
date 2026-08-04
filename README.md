# Dockerfile ANTLR4 Grammar

This project provides a comprehensive ANTLR4 grammar for Dockerfiles.

## Features

- **Dockerfile Support:**
  - Lexer Modes for instructions/arguments.
  - Robust shell command parsing.
  - Recursive `ONBUILD` and `HEALTHCHECK CMD`.
  - Support for nested blocks, lists (block and flow), and key-value pairs.
- **Form Support:** Handles both shell form and exec form (`[...]`) for instructions.
- **Line Continuations:** Correctly handles multi-line instructions using `\` continuation character.
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
scripts/compare_ast.sh testdata/ast-parity/basic.dockerfile
scripts/compare_ast.sh --keep .ast-diff tests/*.dockerfile
```

The adapters project both parser-specific trees into a common JSON document:
instruction name, text or JSON arguments, builder flags, `ONBUILD` children,
heredocs, effective escape token, and source lines. Comments and parser warnings
are not part of the comparison. A unified JSON diff is printed for every
mismatch; `--keep` retains both projections and their diagnostics.

The command exits `0` when all ASTs match or both parsers reject an input, `1`
for an acceptance or AST difference, and `2` for a tooling failure. In addition
to the Java and Maven requirements above, it requires Go at the version declared
in `tools/buildkit-ast/go.mod`. The BuildKit dependency is pinned there so
comparisons are reproducible.

CI runs the strict comparison over every fixture. Any acceptance or AST
difference fails the workflow.

`tests/escape-directive.dockerfile` records that parser directives are currently
comments to the ANTLR lexer: it retains the grammar's backslash escape token
while BuildKit switches to backtick. The fixture remains in strict CI with no
allowlist, so this unsupported feature is reported as a parity failure.

`testdata/ast-parity/heredoc.dockerfile` is an intentional mismatch corpus:
BuildKit accepts it and attaches the heredoc to the `RUN` node, while the
current ANTLR grammar rejects the heredoc body.

## Grammar Design

The grammar uses a `DEFAULT_MODE` to recognize instruction keywords at the start of a line. Once a keyword is found, it switches to `MODE_ARGS` to consume the rest of the line as arguments. This ensures that keywords like `RUN` or `CMD` appearing inside a shell command are treated as literal text rather than new instructions.

`HEALTHCHECK` and `ONBUILD` are handled specially to allow recursive instruction recognition (e.g., `HEALTHCHECK CMD ...`).

## License

BSD 3-Clause. See [LICENSE](LICENSE).
