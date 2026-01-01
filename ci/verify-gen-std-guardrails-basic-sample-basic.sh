#!/usr/bin/env bash
set -euo pipefail

LAYOUT="standard"
GUARDRAILS="basic"
SAMPLE_CODE="basic"
DESCRIPTION="Verify generated project: standard layout, basic guardrails, basic sample"
DEPENDENCIES="web"

source "$(dirname "$0")/common-verify-generated.sh"