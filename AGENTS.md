# Repository Guidance

## Target runtimes

- The intended ANTLR runtimes for this grammar are Swift, Rust, and Python.
- Java is used by the repository's test tooling, but Java compatibility alone is
  not sufficient for grammar changes.

## Grammar portability

- Keep `.g4` files target-neutral and action-free wherever possible.
- For fixes, prefer lexer and parser rules, modes, channels, token types, and
  other portable ANTLR constructs over embedded target-language code.
- Avoid target-specific `@members` blocks, imports, types, method names, actions,
  and semantic predicates. Treat existing uses as portability debt and avoid
  expanding them.
- If target-specific code is unavoidable, isolate it, document why a portable
  grammar solution is not practical, and provide equivalent behavior and
  validation for Swift, Rust, and Python.
