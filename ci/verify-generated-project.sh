#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(pwd)"
WORK_DIR="$(mktemp -d)"
TARGET_DIR="$WORK_DIR/out"

mkdir -p "$TARGET_DIR"

JAR_PATH="$(ls -1 "$ROOT_DIR"/target/codegen-blueprint-*.jar | head -n 1)"
if [ ! -f "$JAR_PATH" ]; then
  echo "Jar not found: $JAR_PATH"
  ls -la "$ROOT_DIR/target" || true
  exit 1
fi

java -jar "$JAR_PATH" \
  springboot \
  --group-id io.github.blueprintplatform \
  --artifact-id greeting \
  --name Greeting \
  --description "Greeting sample built with hexagonal architecture" \
  --package-name io.github.blueprintplatform.greeting \
  --layout hexagonal \
  --enforcement basic \
  --sample-code basic \
  --dependency web \
  --dependency data_jpa \
  --dependency actuator \
  --target-dir "$TARGET_DIR"

ZIP="$TARGET_DIR/greeting.zip"
if [ ! -f "$ZIP" ]; then
  echo "Expected zip not found: $ZIP"
  find "$TARGET_DIR" -maxdepth 2 -type f -print || true
  exit 1
fi

unzip -q "$ZIP" -d "$TARGET_DIR/unzipped"

GEN_DIR="$TARGET_DIR/unzipped/greeting"
if [ ! -d "$GEN_DIR" ]; then
  echo "Expected generated project dir not found: $GEN_DIR"
  find "$TARGET_DIR/unzipped" -maxdepth 3 -type d -print || true
  exit 1
fi

cd "$GEN_DIR"
mvn -q -ntp verify