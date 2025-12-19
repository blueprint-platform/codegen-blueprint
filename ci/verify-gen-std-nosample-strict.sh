#!/usr/bin/env bash
set -euo pipefail

LAYOUT="standard"
SAMPLE_CODE="none"
ENFORCEMENT="strict"
DESCRIPTION="No sample strict enforcement built with standard(layered) architecture"
DEPENDENCIES=""

source "$(dirname "$0")/common-verify-generated.sh"