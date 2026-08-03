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

- `DockerfileLexer.g4`, `DockerfileParser.g4`: Dockerfile grammar.
- `test.dockerfile`: Sample files for testing.
- `tests/`: Directory with additional test cases.

## How to use

1.  **Generate Parser/Lexer:**

    ```bash
    antlr4 DockerfileLexer.g4 DockerfileParser.g4
    ```

2.  **Compile:**

    ```bash
    javac -cp "/path/to/antlr-4.x-complete.jar:." Docker*.java
    ```

3.  **Test Dockerfile:**

    ```bash
    java -cp "/path/to/antlr-4.x-complete.jar:." org.antlr.v4.gui.TestRig Dockerfile dockerfile -tree test.dockerfile
    ```

## Grammar Design

The grammar uses a `DEFAULT_MODE` to recognize instruction keywords at the start of a line. Once a keyword is found, it switches to `MODE_ARGS` to consume the rest of the line as arguments. This ensures that keywords like `RUN` or `CMD` appearing inside a shell command are treated as literal text rather than new instructions.

`HEALTHCHECK` and `ONBUILD` are handled specially to allow recursive instruction recognition (e.g., `HEALTHCHECK CMD ...`).
