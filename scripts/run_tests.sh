#!/bin/bash

# Configuration
ANTLR_JAR="/opt/homebrew/Cellar/antlr/4.13.2/antlr-4.13.2-complete.jar"
GRAMMAR_DIR="grammars"
GEN_DIR="gen"
TESTS_DIR="tests"

# Setup directories
mkdir -p "$GEN_DIR"

if [ ! -f "$ANTLR_JAR" ]; then
    echo "ANTLR jar not found at $ANTLR_JAR"
    exit 1
fi

echo "Generating Lexer and Parser..."
# CD into grammars to avoid tokenVocab path issues
pushd "$GRAMMAR_DIR" > /dev/null
antlr4 -o "../$GEN_DIR" -lib "../$GEN_DIR" -visitor -listener *.g4
popd > /dev/null

echo "Compiling Java files..."
javac -cp "$ANTLR_JAR:." "$GEN_DIR"/*.java

echo "Running Dockerfile tests..."
for f in "$TESTS_DIR"/*.dockerfile; do
    echo "----------------------------------------"
    echo "Testing $f..."
    java -cp "$ANTLR_JAR:$GEN_DIR:." org.antlr.v4.gui.TestRig Dockerfile dockerfile -tree "$f"
done

echo "----------------------------------------"
echo "Tests completed."
