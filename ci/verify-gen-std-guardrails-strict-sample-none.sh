#!/usr/bin/env bash
set -euo pipefail

LAYOUT="standard"
GUARDRAILS="strict"
SAMPLE_CODE="none"
DESCRIPTION="Verify generated project: standard layout, strict guardrails, no sample"
DEPENDENCIES=""

source "$(dirname "$0")/common-verify-generated.sh"