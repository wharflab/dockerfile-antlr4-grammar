#!/usr/bin/env bash
#
# Compare this repository's Dockerfile parse tree with the low-level AST from
# BuildKit's official frontend/dockerfile/parser package.
#
# Both parser-specific trees are projected into the same JSON schema using only
# parser-owned values. The adapters do not normalize source text. Exit 0 means
# every file matched (or both parsers rejected it), exit 1 means a parser/AST
# mismatch, and exit 2 means a tooling failure.

set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
GEN_DIR="$REPO_ROOT/gen"
ANTLR_VERSION="${ANTLR_VERSION:-4.13.2}"
ANTLR_JAR="$REPO_ROOT/.antlr/antlr4-$ANTLR_VERSION-complete.jar"
ANTLR_DUMPER_SOURCE="$REPO_ROOT/tools/antlr-ast/AstDump.java"
BUILDKIT_DUMPER_DIR="$REPO_ROOT/tools/buildkit-ast"
BUILDKIT_DUMPER="$GEN_DIR/buildkit-ast"

usage() {
    cat <<'EOF'
Usage: scripts/compare_ast.sh [--keep DIR] DOCKERFILE [DOCKERFILE ...]

Options:
  --keep DIR  Keep each parser's JSON and diagnostics under DIR.
  -h, --help  Show this help.
EOF
}

die() {
    printf 'ERROR: %s\n' "$*" >&2
    exit 2
}

keep_dir=""
files=()
while [ "$#" -gt 0 ]; do
    case "$1" in
        --keep)
            [ "$#" -ge 2 ] || die "--keep requires a directory"
            keep_dir="$2"
            shift 2
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        --)
            shift
            while [ "$#" -gt 0 ]; do
                files+=("$1")
                shift
            done
            ;;
        -*)
            die "unknown option: $1"
            ;;
        *)
            files+=("$1")
            shift
            ;;
    esac
done

[ "${#files[@]}" -gt 0 ] || {
    usage >&2
    exit 2
}

for tool in java javac mvn go cmp diff; do
    command -v "$tool" >/dev/null 2>&1 || die "'$tool' not found on PATH"
done
for input in "${files[@]}"; do
    [ -f "$input" ] || die "Dockerfile not found: $input"
done

if [ -n "$keep_dir" ]; then
    mkdir -p "$keep_dir" || die "cannot create artifact directory: $keep_dir"
    keep_dir="$(cd "$keep_dir" && pwd)"
fi

printf 'Preparing ANTLR parser...\n'
"$REPO_ROOT/scripts/run_tests.sh" >/dev/null ||
    die "failed to generate or validate the ANTLR parser"
[ -f "$ANTLR_JAR" ] || die "ANTLR jar not found after generation: $ANTLR_JAR"

javac -cp "$ANTLR_JAR:$GEN_DIR" -d "$GEN_DIR" "$ANTLR_DUMPER_SOURCE" ||
    die "failed to compile the ANTLR AST adapter"

printf 'Building BuildKit AST adapter...\n'
(
    cd "$BUILDKIT_DUMPER_DIR" &&
        go build -o "$BUILDKIT_DUMPER" .
) || die "failed to build the BuildKit AST adapter"

temp_dir="$(mktemp -d)" || die "cannot create temporary directory"
trap 'rm -rf "$temp_dir"' EXIT

mismatches=0
index=0
for input in "${files[@]}"; do
    index=$((index + 1))
    base="$(basename "$input")"
    prefix="$temp_dir/$index-$base"
    antlr_json="$prefix.antlr.json"
    antlr_err="$prefix.antlr.err"
    buildkit_json="$prefix.buildkit.json"
    buildkit_err="$prefix.buildkit.err"

    antlr_status=0
    java -cp "$ANTLR_JAR:$GEN_DIR" AstDump "$input" \
        >"$antlr_json" 2>"$antlr_err" || antlr_status=$?

    buildkit_status=0
    "$BUILDKIT_DUMPER" "$input" \
        >"$buildkit_json" 2>"$buildkit_err" || buildkit_status=$?

    if [ "$antlr_status" -ge 2 ]; then
        sed 's/^/  ANTLR: /' "$antlr_err" >&2
        die "ANTLR adapter failed for $input"
    fi
    if [ "$buildkit_status" -ge 2 ]; then
        sed 's/^/  BuildKit: /' "$buildkit_err" >&2
        die "BuildKit adapter failed for $input"
    fi

    if [ "$antlr_status" -eq 0 ] && [ "$buildkit_status" -eq 0 ]; then
        if cmp -s "$antlr_json" "$buildkit_json"; then
            printf 'MATCH        %s\n' "$input"
        else
            mismatches=$((mismatches + 1))
            printf 'AST DIFF     %s\n' "$input"
            diff_status=0
            diff -u "$antlr_json" "$buildkit_json" || diff_status=$?
            [ "$diff_status" -le 1 ] || die "diff failed for $input"
        fi
    elif [ "$antlr_status" -eq 1 ] && [ "$buildkit_status" -eq 1 ]; then
        printf 'BOTH REJECT  %s\n' "$input"
        sed 's/^/  ANTLR: /' "$antlr_err"
        sed 's/^/  BuildKit: /' "$buildkit_err"
    else
        mismatches=$((mismatches + 1))
        printf 'PARSE DIFF   %s\n' "$input"
        if [ "$antlr_status" -eq 1 ]; then
            printf '  ANTLR rejected; BuildKit accepted.\n'
            sed 's/^/  ANTLR: /' "$antlr_err"
        else
            printf '  BuildKit rejected; ANTLR accepted.\n'
            sed 's/^/  BuildKit: /' "$buildkit_err"
        fi
    fi

    if [ -n "$keep_dir" ]; then
        cp "$antlr_json" "$keep_dir/$index-$base.antlr.json" ||
            die "cannot retain ANTLR AST for $input"
        cp "$antlr_err" "$keep_dir/$index-$base.antlr.err" ||
            die "cannot retain ANTLR diagnostics for $input"
        cp "$buildkit_json" "$keep_dir/$index-$base.buildkit.json" ||
            die "cannot retain BuildKit AST for $input"
        cp "$buildkit_err" "$keep_dir/$index-$base.buildkit.err" ||
            die "cannot retain BuildKit diagnostics for $input"
    fi
done

printf '\nCompared %d file(s): %d mismatch(es).\n' "${#files[@]}" "$mismatches"
[ "$mismatches" -eq 0 ]
