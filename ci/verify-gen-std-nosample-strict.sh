#!/usr/bin/env bash
set -euo pipefail

LAYOUT="standard"
SAMPLE_CODE="none"
GUARDRAILS="strict"
DESCRIPTION="No sample strict guardrails built with standard(layered) architecture"
DEPENDENCIES=""

source "$(dirname "$0")/common-verify-generated.sh"