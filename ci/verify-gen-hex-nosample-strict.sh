#!/usr/bin/env bash
set -euo pipefail

LAYOUT="hexagonal"
SAMPLE_CODE="none"
GUARDRAILS="strict"
DESCRIPTION="No sample strict guardrails with hexagonal architecture"
DEPENDENCIES=""

source "$(dirname "$0")/common-verify-generated.sh"