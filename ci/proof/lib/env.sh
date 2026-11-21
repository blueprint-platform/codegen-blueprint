# shellcheck shell=bash
# Environment + path initialization (sourced)
# -------------------------
# Config (env-overridable)
# -------------------------
GROUP_ID="${GROUP_ID:-io.github.blueprintplatform}"
PACKAGE_NAME="${PACKAGE_NAME:-io.github.blueprintplatform.greeting}"
JAVA_VERSION="${JAVA_VERSION:-25}"
JAVA_BIN="${JAVA_BIN:-${GENERATOR_JAVA:-${JAVA_HOME:+$JAVA_HOME/bin/}java}}"

KEEP_WORK_DIR="${KEEP_WORK_DIR:-0}"
PROOF_EXPORT_DIR="${PROOF_EXPORT_DIR:-}"

# Proof output root (in-repo by default)
# Can be overridden to write elsewhere (e.g., CI workspace)
PROOF_OUTPUT_ROOT="${PROOF_OUTPUT_ROOT:-}"

# -------------------------
# Globals (late-init)
# -------------------------
ROOT_DIR="${ROOT_DIR:-}"
CODEGEN_JAR="${CODEGEN_JAR:-}"


WORK_DIR="${WORK_DIR:-}"
OUT_DIR="${OUT_DIR:-}"
BACKUP_DIR="${BACKUP_DIR:-}"

RUN_TS="${RUN_TS:-}"
RUN_DIR="${RUN_DIR:-}"

LOG_DIR="${LOG_DIR:-}"
EXCERPT_DIR="${EXCERPT_DIR:-}"

ENV_FILE="${ENV_FILE:-}"
SUMMARY_FILE="${SUMMARY_FILE:-}"


# -------------------------
# Repo root detection
# -------------------------
ensure_repo_root() {
  # 1) Prefer git if available and we are inside a repo
  if command -v git >/dev/null 2>&1; then
    local git_root
    git_root="$(git rev-parse --show-toplevel 2>/dev/null || true)"
    if [[ -n "$git_root" && -d "$git_root" ]]; then
      echo "$git_root"
      return 0
    fi
  fi

  # 2) Fallback: walk up from this lib dir and find a plausible repo root
  # Heuristics: .git directory OR pom.xml at root
  local dir
  dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

  for _ in 1 2 3 4 5 6 7 8; do
    if [[ -d "${dir}/.git" || -f "${dir}/pom.xml" ]]; then
      echo "$dir"
      return 0
    fi
    local parent
    parent="$(cd "${dir}/.." && pwd)"
    [[ "$parent" == "$dir" ]] && break
    dir="$parent"
  done

  die "Could not infer repository root. Run from inside the repo or install git."
}

# -------------------------
# Context init
# -------------------------
require_proof_jdk() {
  case "$JAVA_VERSION" in
    21|25) ;;
    *) die "Unsupported proof target JAVA_VERSION=$JAVA_VERSION. Choose 21 or 25." ;;
  esac

  # Match the generated Maven wrapper: JAVA_HOME takes precedence over PATH.
  local bin="${JAVA_HOME:+$JAVA_HOME/bin/}" runtime compiler
  local selected="${JAVA_HOME:+JAVA_HOME=$JAVA_HOME}"
  selected="${selected:-PATH}"
  local guidance="Set JAVA_HOME to an installed JDK $JAVA_VERSION for this invocation, or choose JAVA_VERSION=21 for a Java 21 proof."
  if ! runtime="$("${bin}java" -version 2>&1)" || ! compiler="$("${bin}javac" -version 2>&1)"; then
    die "Proof targeting Java $JAVA_VERSION requires a JDK. Could not run java and javac using $selected. $guidance"
  fi
  local runtime_pattern='(openjdk|java) version "([0-9]+)'
  local compiler_pattern='javac ([0-9]+)'
  if [[ ! $runtime =~ $runtime_pattern ]]; then
    die "Cannot determine Maven's Java version using $selected. $guidance"
  fi
  local runtime_major="${BASH_REMATCH[2]}"
  if [[ ! $compiler =~ $compiler_pattern ]]; then
    die "Cannot determine the compiler version using $selected. $guidance"
  fi
  if (( runtime_major < JAVA_VERSION || BASH_REMATCH[1] < JAVA_VERSION )); then
    die "Proof targets Java $JAVA_VERSION, but Maven would use Java $runtime_major via $selected ($compiler). $guidance"
  fi
}

init_runtime_context() {
  # This file lives under: ci/proof/lib

  ROOT_DIR="$(ensure_repo_root)"

  # Default JAR location if not provided via env
  if [[ -z "$CODEGEN_JAR" ]]; then
    local final_name
    final_name="$(mvn -f "$ROOT_DIR/pom.xml" -q -ntp help:evaluate -Dexpression=project.build.finalName -DforceStdout)"
    CODEGEN_JAR="$ROOT_DIR/target/$final_name.jar"
  fi

  # Keep local proof output under the Maven build directory.
  PROOF_OUTPUT_ROOT="${PROOF_OUTPUT_ROOT:-${ROOT_DIR}/target/architecture-proof}"

  # Workspace (temp)
  WORK_DIR="$(mktemp -d -t codegen-proof-XXXXXX)"
  OUT_DIR="${WORK_DIR}/out"

  # Backup store OUTSIDE generated project tree (temp)
  BACKUP_DIR="${WORK_DIR}/.backup"
  mkdir -p "${BACKUP_DIR}"

  # Run output dirs (in-repo by default)
  RUN_TS="$(date +%Y%m%d-%H%M%S)"
  RUN_DIR="${PROOF_OUTPUT_ROOT}/${RUN_TS}"

  LOG_DIR="${RUN_DIR}/logs"
  EXCERPT_DIR="${RUN_DIR}/excerpts"
  mkdir -p "${LOG_DIR}" "${EXCERPT_DIR}"

  ENV_FILE="${RUN_DIR}/env.txt"
  SUMMARY_FILE="${RUN_DIR}/proof-summary.txt"
}
