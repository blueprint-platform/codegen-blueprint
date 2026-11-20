#!/usr/bin/env bash
set -euo pipefail

jar="${1:?Pass the packaged Codegen Blueprint JAR}"
mode="${2:-refresh}"
case "$mode" in
  refresh|--check) ;;
  *) printf 'Unsupported snapshot mode: %s\n' "$mode" >&2; exit 2 ;;
esac
root="$(cd "$(dirname "$0")/.." && pwd)"
scratch="$(mktemp -d)"
trap 'rm -rf "$scratch"' EXIT

for layout in standard hexagonal; do
  java -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=1 -jar "$jar" --cli springboot \
    --boot 4.2 --java 25 --layout "$layout" --guardrails strict --sample-code basic \
    --group-id com.example --artifact-id "greeting-$layout" --name Greeting \
    --description "Greeting sample with executable architecture" \
    --package-name com.example.greeting --target-dir "$scratch" > "$scratch/$layout.log"
  grep -F 'source=BUNDLED_FALLBACK' "$scratch/$layout.log"
done
for layout in standard hexagonal; do
  generated="$scratch/greeting-$layout/README.md"
  snapshot="$root/docs/examples/$layout.md"
  if [[ "$mode" == --check ]]; then
    diff -u "$snapshot" "$generated"
  else
    cp "$generated" "$snapshot"
  fi
done
