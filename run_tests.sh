#!/bin/bash

ANTLR_JAR="/opt/homebrew/Cellar/antlr/4.13.2/antlr-4.13.2-complete.jar"

if [ ! -f "$ANTLR_JAR" ]; then
    echo "ANTLR jar not found at $ANTLR_JAR"
    exit 1
fi

echo "Generating Lexer and Parser for Dockerfile..."
antlr4 DockerfileLexer.g4 DockerfileParser.g4
echo "Generating Lexer and Parser for DockerCompose..."
antlr4 DockerComposeLexer.g4 DockerComposeParser.g4

echo "Compiling Java files..."
javac -cp "$ANTLR_JAR:." Docker*.java

echo "Running Dockerfile tests..."
for f in tests/*.dockerfile test.dockerfile; do
    echo "----------------------------------------"
    echo "Testing $f..."
    java -cp "$ANTLR_JAR:." org.antlr.v4.gui.TestRig Dockerfile dockerfile -tree "$f"
done

echo "Running DockerCompose tests..."
echo "----------------------------------------"
echo "Testing test-compose.yml..."
java -cp "$ANTLR_JAR:." org.antlr.v4.gui.TestRig DockerCompose composeFile -tree test-compose.yml

echo "----------------------------------------"
echo "Tests completed."
