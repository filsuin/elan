#!/bin/sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
DEST="$ROOT/gradle/wrapper/gradle-wrapper.jar"
EXPECTED=81a82aaea5abcc8ff68b3dfcb58b3c3c429378efd98e7433460610fecd7ae45f
URL=https://raw.githubusercontent.com/gradle/gradle/v8.13.0/gradle/wrapper/gradle-wrapper.jar
hash_file() {
    if command -v sha256sum >/dev/null 2>&1; then sha256sum "$1" | awk '{print $1}'
    elif command -v shasum >/dev/null 2>&1; then shasum -a 256 "$1" | awk '{print $1}'
    else echo 'sha256sum or shasum is required.' >&2; exit 1; fi
}
if [ -f "$DEST" ]; then
    [ "$(hash_file "$DEST")" = "$EXPECTED" ] || { echo 'Existing wrapper checksum differs. Inspect it before replacing it.' >&2; exit 1; }
    echo 'Official Gradle wrapper already present and verified.'
    exit 0
fi
mkdir -p "$(dirname "$DEST")"
TEMP=$(mktemp "$ROOT/gradle/wrapper/wrapper.XXXXXX")
trap 'rm -f "$TEMP"' EXIT HUP INT TERM
if command -v curl >/dev/null 2>&1; then
    curl --fail --location --retry 2 --connect-timeout 15 --max-time 120 "$URL" -o "$TEMP"
elif command -v wget >/dev/null 2>&1; then
    wget --timeout=120 -O "$TEMP" "$URL"
else echo 'Install curl or wget first.' >&2; exit 1; fi
[ "$(hash_file "$TEMP")" = "$EXPECTED" ] || { echo 'Checksum verification failed. Nothing was installed.' >&2; exit 1; }
mv "$TEMP" "$DEST"
echo 'Ready. Open this project in Android Studio.'
