#!/usr/bin/env bash
#
# Generates the Dockerfile lexer/parser, compiles them, and parses every fixture
# in tests/ to verify the grammar accepts them.
#
# Portability: needs only a JDK (java + javac), Maven, and coreutils. The ANTLR
# tool is fetched from Maven Central into an isolated in-repo Maven repository,
# so the result does not depend on Homebrew or on whatever happens to be cached
# in the user's shared ~/.m2.
#
# Usage:
#   scripts/run_tests.sh            # generate, compile, parse all fixtures
#   scripts/run_tests.sh -t         # also print each parse tree
#   ANTLR_VERSION=4.13.2 ...        # override the pinned ANTLR version

set -euo pipefail

# --- Configuration -----------------------------------------------------------

ANTLR_VERSION="${ANTLR_VERSION:-4.13.2}"

# SHA-256 of the antlr4-<version>-complete.jar we expect. Maven Central publishes
# only .sha1/.md5 for this artifact, so the strong hash is pinned here instead.
# Verified against both Maven Central and the Homebrew build (identical bytes).
# Read below via indirect expansion, which shellcheck cannot follow.
# shellcheck disable=SC2034
ANTLR_SHA256_4_13_2="eae2dfa119a64327444672aff63e9ec35a20180dc5b8090b7a6ab85125df4d76"

# Resolve paths relative to the repo root so the script works from any cwd.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

GRAMMAR_DIR="$REPO_ROOT/grammars"
GEN_DIR="$REPO_ROOT/gen"
TESTS_DIR="$REPO_ROOT/tests"
TOOL_DIR="$REPO_ROOT/.antlr"           # git-ignored: fetched tool + isolated m2
ANTLR_JAR="$TOOL_DIR/antlr4-$ANTLR_VERSION-complete.jar"

SHOW_TREE=""
case "${1:-}" in
    -t|--tree) SHOW_TREE="-tree" ;;
    "") ;;
    *) printf 'Usage: %s [-t|--tree]\n' "$(basename "$0")" >&2; exit 2 ;;
esac

# The Java launcher announces these on stderr for every JVM it starts ("Picked up
# JAVA_TOOL_OPTIONS: ...", and "NOTE: Picked up JDK_JAVA_OPTIONS: ..."). Parse
# failures are detected via stderr below, so an inherited value here would make
# every fixture report a spurious failure. Drop them for the JVMs we launch;
# use ANTLR_VERSION or edit this script if JVM tuning is genuinely needed.
unset JAVA_TOOL_OPTIONS _JAVA_OPTIONS JDK_JAVA_OPTIONS

# --- Helpers -----------------------------------------------------------------

die() { printf '\nERROR: %s\n' "$*" >&2; exit 1; }

# sha256 of a file, on either macOS (shasum) or Linux (sha256sum).
sha256_of() {
    if command -v shasum >/dev/null 2>&1; then
        shasum -a 256 "$1" | cut -d' ' -f1
    elif command -v sha256sum >/dev/null 2>&1; then
        sha256sum "$1" | cut -d' ' -f1
    else
        die "need shasum or sha256sum to verify the ANTLR download"
    fi
}

# --- Preflight ---------------------------------------------------------------

for tool in java javac mvn; do
    command -v "$tool" >/dev/null 2>&1 || die "'$tool' not found on PATH. Install a JDK and Maven."
done

[ -d "$GRAMMAR_DIR" ] || die "grammar directory not found: $GRAMMAR_DIR"

# --- Fetch the ANTLR tool ----------------------------------------------------

# Expected hash for the requested version, if we have one pinned. Indirect
# expansion keeps this working on bash 3.2 (macOS /bin/bash), which has no
# associative arrays.
sha_var="ANTLR_SHA256_$(printf '%s' "$ANTLR_VERSION" | tr '.' '_')"
expected_sha="${!sha_var:-}"

# Re-verify on every run: a jar that was correct when downloaded can be replaced
# later, and Maven does not re-check artifacts already in its cache.
if [ -f "$ANTLR_JAR" ] && [ -n "$expected_sha" ] &&
   [ "$(sha256_of "$ANTLR_JAR")" != "$expected_sha" ]; then
    echo "Cached ANTLR jar failed checksum verification; re-fetching."
    rm -f "$ANTLR_JAR"
fi

if [ ! -f "$ANTLR_JAR" ]; then
    echo "Fetching ANTLR $ANTLR_VERSION from Maven Central..."
    mkdir -p "$TOOL_DIR"
    # An in-repo repository, deliberately NOT the shared ~/.m2: this keeps the
    # build reproducible even if the user's cache holds a divergent jar.
    mvn -q -B \
        "-Dmaven.repo.local=$TOOL_DIR/m2" \
        org.apache.maven.plugins:maven-dependency-plugin:3.6.1:copy \
        "-Dartifact=org.antlr:antlr4:$ANTLR_VERSION:jar:complete" \
        "-DoutputDirectory=$TOOL_DIR" \
        || die "Maven could not download org.antlr:antlr4:$ANTLR_VERSION (offline, or version does not exist?)"
    [ -f "$ANTLR_JAR" ] || die "expected jar missing after download: $ANTLR_JAR"
fi

if [ -n "$expected_sha" ]; then
    actual_sha="$(sha256_of "$ANTLR_JAR")"
    [ "$actual_sha" = "$expected_sha" ] || die \
"ANTLR jar checksum mismatch for $ANTLR_JAR
  expected sha256: $expected_sha
  actual   sha256: $actual_sha
Refusing to run against an unverified tool. Delete $TOOL_DIR and retry."
    echo "Using ANTLR $ANTLR_VERSION (sha256 verified)."
else
    echo "Using ANTLR $ANTLR_VERSION (no pinned checksum for this version; not verified)."
fi

# --- Generate ----------------------------------------------------------------

# Start from a clean gen/ so stale generated sources can never mask a failure.
rm -rf "$GEN_DIR"
mkdir -p "$GEN_DIR"

echo "Generating lexer and parser..."
# Run from grammars/ so the parser's `tokenVocab=DockerfileLexer` resolves: the
# lexer must be processed first and its .tokens file found via -lib. Passing bare
# filenames (not absolute paths) also keeps output flat in gen/ rather than
# mirroring the source tree.
(
    cd "$GRAMMAR_DIR"
    java -jar "$ANTLR_JAR" \
        -o "$GEN_DIR" -lib "$GEN_DIR" \
        -visitor -listener \
        DockerfileLexer.g4 DockerfileParser.g4
) || die "ANTLR failed to generate the parser"

# --- Compile -----------------------------------------------------------------

echo "Compiling generated Java sources..."
javac -cp "$ANTLR_JAR" -d "$GEN_DIR" "$GEN_DIR"/*.java \
    || die "javac failed to compile the generated parser"

# TestRig prints "Can't load X as lexer or parser" and still exits 0, which would
# otherwise look like a per-fixture parse error. Fail loudly and once instead.
for class in DockerfileLexer DockerfileParser; do
    [ -f "$GEN_DIR/$class.class" ] || die "expected $class.class in $GEN_DIR after compiling"
done

# --- Parse fixtures ----------------------------------------------------------

# TestRig has no switch to make it fail: its only catch handles NoSuchMethodException,
# syntax errors go to the default ConsoleErrorListener, and main() never calls
# System.exit -- so it ALWAYS exits 0 and its status is useless as a signal.
# Treat any stderr output as failure; all fixtures are expected to parse silently.
# The `dockerfile` rule requires EOF, so a partial parse also surfaces as an error
# here rather than passing quietly.
#
# Deliberately one fixture per invocation: given multiple input files TestRig
# echoes each filename to stderr, which would defeat the check below.
#
# -encoding UTF-8 is explicit because TestRig otherwise decodes with the JVM
# default charset, which varies with the runner's locale (on JDK 17 under LANG=C
# it degrades to ASCII). Note this must be TestRig's own flag, not
# -Dfile.encoding via JAVA_TOOL_OPTIONS, which the launcher echoes to stderr
# (hence the unset above).
echo "Parsing fixtures in ${TESTS_DIR#"$REPO_ROOT"/}/..."

pass=0
fail=0
edge_pass=0
edge_fail=0
failed_files=""
stderr_file="$(mktemp)"
trap 'rm -f "$stderr_file"' EXIT

shopt -s nullglob
fixtures=("$TESTS_DIR"/*.dockerfile)
shopt -u nullglob

[ "${#fixtures[@]}" -gt 0 ] || die "no *.dockerfile fixtures found in $TESTS_DIR"

for f in "${fixtures[@]}"; do
    name="$(basename "$f")"

    rc=0
    if [ -n "$SHOW_TREE" ]; then
        java -cp "$ANTLR_JAR:$GEN_DIR" org.antlr.v4.gui.TestRig \
            Dockerfile dockerfile -encoding UTF-8 -tree "$f" 2>"$stderr_file" || rc=$?
    else
        java -cp "$ANTLR_JAR:$GEN_DIR" org.antlr.v4.gui.TestRig \
            Dockerfile dockerfile -encoding UTF-8 "$f" >/dev/null 2>"$stderr_file" || rc=$?
    fi

    # Fail on parse diagnostics (stderr, exit 0) or on the JVM itself dying
    # (nonzero exit, possibly with no stderr at all -- e.g. an OOM kill).
    if [ -s "$stderr_file" ] || [ "$rc" -ne 0 ]; then
        fail=$((fail + 1))
        failed_files="$failed_files  $name"$'\n'
        printf '  FAIL  %s\n' "$name"
        [ "$rc" -eq 0 ] || printf '          (java exited %s)\n' "$rc"
        sed 's/^/          /' "$stderr_file"
    else
        pass=$((pass + 1))
        printf '  ok    %s\n' "$name"
    fi
done

echo
echo "Checking parser-directive edge cases..."

check_directive_case() {
    name="$1"
    expectation="$2"
    source="$3"

    : >"$stderr_file"
    rc=0
    printf '%s' "$source" |
        java -cp "$ANTLR_JAR:$GEN_DIR" org.antlr.v4.gui.TestRig \
            Dockerfile dockerfile -encoding UTF-8 \
            >/dev/null 2>"$stderr_file" || rc=$?

    matched=""
    if [ "$rc" -eq 0 ]; then
        case "$expectation" in
            accept) [ ! -s "$stderr_file" ] && matched=1 ;;
            reject) [ -s "$stderr_file" ] && matched=1 ;;
        esac
    fi

    if [ -n "$matched" ]; then
        edge_pass=$((edge_pass + 1))
        printf '  ok    %s\n' "$name"
    else
        edge_fail=$((edge_fail + 1))
        failed_files="$failed_files  $name"$'\n'
        printf '  FAIL  %s (expected %s)\n' "$name" "$expectation"
        [ "$rc" -eq 0 ] || printf '          (java exited %s)\n' "$rc"
        sed 's/^/          /' "$stderr_file"
    fi
}

check_directive_case \
    "empty escape directive" accept \
    $'# escape=\nFROM alpine\n'
check_directive_case \
    "whitespace-only escape directive" reject \
    $'# escape=   \nFROM alpine\n'

# --- Summary -----------------------------------------------------------------

echo
if [ "$fail" -eq 0 ] && [ "$edge_fail" -eq 0 ]; then
    echo "All $pass fixture(s) parsed cleanly; all $edge_pass edge case(s) passed."
    exit 0
fi

printf '%s validation(s) failed:\n%s' "$((fail + edge_fail))" "$failed_files"
exit 1
