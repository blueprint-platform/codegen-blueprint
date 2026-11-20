#!/usr/bin/env bash
set -euo pipefail

version=3.9.16
checksum=5af3b743dd8b876b5c45da33b676251e5f1687712644abb4ee519ca56e1d89ce
distribution="apache-maven-$version"
archive="$RUNNER_TEMP/$distribution-bin.zip"
curl --fail --silent --show-error --location \
  "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$version/$distribution-bin.zip" \
  --output "$archive"
printf '%s  %s\n' "$checksum" "$archive" | sha256sum --check -
unzip -q "$archive" -d "$RUNNER_TEMP"
printf '%s\n' "$RUNNER_TEMP/$distribution/bin" >> "$GITHUB_PATH"
