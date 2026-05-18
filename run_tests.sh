#!/bin/bash

ANTLR_JAR="/opt/homebrew/Cellar/antlr/4.13.2/antlr-4.13.2-complete.jar"

if [ ! -f "$ANTLR_JAR" ]; then
    echo "ANTLR jar not found at $ANTLR_JAR"
    exit 1
fi

echo "Generating Lexer and Parser..."
antlr4 DockerfileLexer.g4 DockerfileParser.g4

echo "Compiling Java files..."
javac -cp "$ANTLR_JAR:." Dockerfile*.java

echo "Running tests..."
for f in tests/*.dockerfile test.dockerfile; do
    echo "----------------------------------------"
    echo "Testing $f..."
    java -cp "$ANTLR_JAR:." org.antlr.v4.gui.TestRig Dockerfile dockerfile -tree "$f"
done

echo "----------------------------------------"
echo "Tests completed."
