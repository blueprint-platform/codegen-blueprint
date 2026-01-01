#!/usr/bin/env bash
set -euo pipefail

LAYOUT="hexagonal"
GUARDRAILS="strict"
SAMPLE_CODE="none"
DESCRIPTION="Verify generated project: hexagonal layout, strict guardrails, no sample"
DEPENDENCIES=""

source "$(dirname "$0")/common-verify-generated.sh"