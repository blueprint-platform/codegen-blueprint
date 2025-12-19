#!/usr/bin/env bash
set -euo pipefail

LAYOUT="hexagonal"
SAMPLE_CODE="none"
ENFORCEMENT="strict"
DESCRIPTION="No sample strict enforcement with hexagonal architecture"
DEPENDENCIES=""

source "$(dirname "$0")/common-verify-generated.sh"