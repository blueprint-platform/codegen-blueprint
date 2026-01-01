#!/usr/bin/env bash
set -euo pipefail

LAYOUT="hexagonal"
GUARDRAILS="strict"
SAMPLE_CODE="basic"
DESCRIPTION="Verify generated project: hexagonal layout, strict guardrails, basic sample"
DEPENDENCIES="web"

source "$(dirname "$0")/common-verify-generated.sh"