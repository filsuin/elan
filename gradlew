#!/bin/sh
# Minimal launcher; the downloaded GradleWrapperMain is the official verified binary.
set -eu
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
sh "$APP_HOME/tools/prepare-wrapper.sh"
if [ -n "${JAVA_HOME:-}" ]; then JAVA="$JAVA_HOME/bin/java"; else JAVA=java; fi
if ! "$JAVA" -version >/dev/null 2>&1; then
    echo 'JDK 17 or 21 is required. Set JAVA_HOME or use Android Studio.' >&2
    exit 1
fi
exec "$JAVA" -Dorg.gradle.appname=gradlew -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
