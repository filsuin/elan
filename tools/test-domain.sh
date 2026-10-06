#!/bin/sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
command -v kotlinc >/dev/null 2>&1 || { echo "Install Kotlin CLI 1.9+ and JDK 17+ first." >&2; exit 1; }
OUT=$(mktemp -d)
trap 'rm -rf "$OUT"' EXIT HUP INT TERM
kotlinc "$ROOT"/app/src/main/java/fr/elan/app/domain/*.kt \
    "$ROOT"/app/src/main/java/fr/elan/app/content/*.kt \
    "$ROOT/tools/DomainSmokeTest.kt" -include-runtime -d "$OUT/domain-tests.jar"
java -jar "$OUT/domain-tests.jar"
