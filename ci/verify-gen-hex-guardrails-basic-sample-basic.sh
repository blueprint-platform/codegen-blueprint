#!/usr/bin/env bash
set -euo pipefail

LAYOUT="hexagonal"
GUARDRAILS="basic"
SAMPLE_CODE="basic"
DESCRIPTION="Verify generated project: hexagonal layout, basic guardrails, basic sample"
DEPENDENCIES="web"

source "$(dirname "$0")/common-verify-generated.sh"