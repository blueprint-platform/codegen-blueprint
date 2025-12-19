#!/usr/bin/env bash
set -euo pipefail

LAYOUT="standard"
SAMPLE_CODE="basic"
ENFORCEMENT="strict"
DESCRIPTION="Greeting sample strict enforcement built with standard(layered) architecture"
DEPENDENCIES="web data_jpa actuator"

source "$(dirname "$0")/common-verify-generated.sh"