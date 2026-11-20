#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."

mode="${1:-standard}"
case "$mode" in
  standard|full) ;;
  *) echo "Usage: bash ci/verify-generated.sh [standard|full]" >&2; exit 2 ;;
esac

export BOOT_VERSION="${BOOT_VERSION:-4.2}"
export JAVA_VERSION="${JAVA_VERSION:-25}"
export EXPECT_BOOT_SOURCE=BUNDLED_FALLBACK

verify() {
  LAYOUT="$1" GUARDRAILS="$2" SAMPLE_CODE="$3" DEPENDENCIES="${4:-}" \
    bash ci/verify-project.sh
}

if [[ "$mode" == full ]]; then
  for layout in standard hexagonal; do
    verify "$layout" strict basic
    verify "$layout" strict none
    verify "$layout" basic basic web
  done
  verify standard none none
  verify hexagonal strict basic 'data_jpa security validation actuator devtools'
  if [[ "$BOOT_VERSION" == 4.1 ]]; then
    VERIFY_BOOT_4_1_0_EDGE=1 verify hexagonal strict basic
    VERIFY_BOOT_4_1_0_EDGE=1 verify standard none none
  fi
  bash ci/proof/proof-runner.sh
else
  case "$BOOT_VERSION/$JAVA_VERSION" in
    4.1/21) verify standard basic basic web ;;
    4.1/25) verify hexagonal strict basic ;;
    4.2/21) verify hexagonal strict basic 'data_jpa security' ;;
    4.2/25)
      verify standard strict basic
      verify standard none none
      verify hexagonal strict none
      PROOF_MODE=standard bash ci/proof/proof-runner.sh
      ;;
    *) echo "Unsupported verification cell: $BOOT_VERSION/$JAVA_VERSION" >&2; exit 2 ;;
  esac
fi
