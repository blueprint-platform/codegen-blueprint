#!/usr/bin/env bash
set -euo pipefail

LAYOUT="hexagonal"
SAMPLE_CODE="basic"
ENFORCEMENT="strict"
DESCRIPTION="Greeting sample strict enforcement built with hexagonal architecture"
DEPENDENCIES="web data_jpa actuator"

source "$(dirname "$0")/common-verify-generated.sh"