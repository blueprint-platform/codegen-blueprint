#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(pwd)"
WORK_DIR="$(mktemp -d)"
TARGET_DIR="$WORK_DIR/out"

JAVA_VERSION="${JAVA_VERSION:-25}"
BOOT_VERSION="${BOOT_VERSION:-4.2}"
ARTIFACT_ID="${ARTIFACT_ID:-greeting}"
PACKAGE_NAME="${PACKAGE_NAME:-io.github.blueprintplatform.greeting}"

LAYOUT="${LAYOUT:?LAYOUT is required (hexagonal|standard)}"
GUARDRAILS="${GUARDRAILS:-strict}"
SAMPLE_CODE="${SAMPLE_CODE:?SAMPLE_CODE is required (none|basic)}"
DESCRIPTION="${DESCRIPTION:-Generated project verification}"
DEPENDENCIES="${DEPENDENCIES:-}"

cleanup() {
  rm -rf "$WORK_DIR" || true
}
trap cleanup EXIT

mkdir -p "$TARGET_DIR"

JAR_PATH="${CODEGEN_JAR:-}"
if [[ -z "$JAR_PATH" ]]; then
  FINAL_NAME="$(mvn -q -ntp help:evaluate -Dexpression=project.build.finalName -DforceStdout)"
  JAR_PATH="$ROOT_DIR/target/$FINAL_NAME.jar"
fi
test -f "$JAR_PATH"

CLI_ARGS=(
  --spring.main.web-application-type=none
  --cli springboot
  --group-id io.github.blueprintplatform
  --artifact-id "$ARTIFACT_ID"
  --name Greeting
  --description "$DESCRIPTION"
  --package-name "$PACKAGE_NAME"
  --layout "$LAYOUT"
  --guardrails "$GUARDRAILS"
  --sample-code "$SAMPLE_CODE"
  --java "$JAVA_VERSION"
  --boot "$BOOT_VERSION"
  --target-dir "$TARGET_DIR"
)

if [[ -n "${DEPENDENCIES:-}" ]]; then
  # shellcheck disable=SC2206
  for dep in $DEPENDENCIES; do
    CLI_ARGS+=(--dependency "$dep")
  done
fi

GENERATOR_OPTS=(-jar)
if [[ "${EXPECT_BOOT_SOURCE:-}" == BUNDLED_FALLBACK ]]; then
  GENERATOR_OPTS=(-Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=1 -jar)
fi
"${GENERATOR_JAVA:-java}" "${GENERATOR_OPTS[@]}" "$JAR_PATH" "${CLI_ARGS[@]}" | tee "$WORK_DIR/cli.log"
if [[ -n "${EXPECT_BOOT_SOURCE:-}" ]]; then
  grep -F "source=$EXPECT_BOOT_SOURCE" "$WORK_DIR/cli.log"
fi

ZIP="$TARGET_DIR/$ARTIFACT_ID.zip"
if [[ ! -f "$ZIP" ]]; then
  echo "Expected zip not found: $ZIP"
  find "$TARGET_DIR" -maxdepth 3 -type f -print || true
  exit 1
fi

unzip -q "$ZIP" -d "$TARGET_DIR/unzipped"

GEN_DIR="$TARGET_DIR/unzipped/$ARTIFACT_ID"
if [[ ! -d "$GEN_DIR" ]]; then
  echo "Expected generated project dir not found: $GEN_DIR"
  find "$TARGET_DIR/unzipped" -maxdepth 4 -type d -print || true
  exit 1
fi

cd "$GEN_DIR"

if [[ "${VERIFY_BOOT_4_1_0_EDGE:-0}" == 1 ]]; then
  # Only this temporary fixture uses an exact patch.
  [[ "$BOOT_VERSION" == 4.1 && "${EXPECT_BOOT_SOURCE:-}" == BUNDLED_FALLBACK ]]
  python3 - "$WORK_DIR/cli.log" <<'PYTHON'
from pathlib import Path
import re
import sys

log = Path(sys.argv[1]).read_text()
match = re.search(r"bootVersion=(4[.]1[.][0-9]+) source=BUNDLED_FALLBACK", log)
assert match, "Missing 4.1 fallback patch"
pom = Path("pom.xml")
original = pom.read_text(encoding="utf-8")
parent = f"<version>{match[1]}</version>"
assert original.count(parent) == 1, "Expected one generated parent patch"
print(f"Minimum compatibility fixture: {match[1]} -> 4.1.0")
pom.write_text(original.replace(parent, "<version>4.1.0</version>"), encoding="utf-8")
PYTHON
fi

chmod +x ./mvnw
./mvnw -B -ntp clean verify
main_class="$(find target/classes -name '*Application.class' -print -quit)"
test -f "$main_class"
javap -verbose "$main_class" | grep -F "major version: $((JAVA_VERSION + 44))"
if [[ "$SAMPLE_CODE" == basic ]]; then
  for suite in GreetingSampleHttpIT GreetingControllerWebMvcIT; do
    report="$(find target/failsafe-reports -name "TEST-*.$suite.xml" -print -quit)"
    test -f "$report"
    grep -El 'tests="[1-9][0-9]*"' "$report"
  done
fi
if [[ "$GUARDRAILS" != none ]]; then
  find target/surefire-reports -name 'TEST-*.architecture.archunit.*Test.xml' | grep .
else
  if grep -q 'archunit-junit' pom.xml || find src/test -path '*/architecture/archunit/*' | grep -q .; then
    echo "Guardrails unexpectedly generated for none" >&2
    exit 1
  fi
fi
