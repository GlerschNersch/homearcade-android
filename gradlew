#!/bin/sh
# Gradle wrapper
exec java -jar "$(dirname "$0")/gradle/wrapper/gradle-wrapper.jar" "$@"
