#!/usr/bin/env python3
"""Write a portable excerpt from a successful proof-runner result."""

from pathlib import Path
import re
import sys

run = Path(sys.argv[1])
summary = (run / "proof-summary.txt").read_text(encoding="utf-8")
if "PROOF RESULT: PASS" not in summary:
    raise SystemExit("Proof runner did not complete successfully")
env = (run / "env.txt").read_text(encoding="utf-8")
print("Codegen Blueprint 2.0 executable proof")
print(re.search(r"^timestamp: .+$", env, re.M).group())
print(re.search(r'^java version .+$', env, re.M).group())
print("JAR SHA-256: " + re.search(r"codegen_jar_sha256:\n([a-f0-9]{64})", env)[1])
maven = re.search(r"Apache Maven ([\d.]+)", env)
if maven:
    print("Apache Maven " + maven[1])
print()
for layout in ("hex", "standard"):
    log = (run / f"logs/generate-greeting-{layout}.log").read_text(encoding="utf-8")
    print(layout + ": " + re.search(r"bootSelector=\S+ bootVersion=\S+ source=\S+", log).group())
print()
for name, status in re.findall(r"^  - (.*?)\s*:\s*(PASS|EXPECTED_FAIL)\s*:", summary, re.M):
    print(f"{status}: {name.strip()}")
print()
for name in ("HEX_violation", "STD_schema_violation", "STD_violation", "HEX_REST_signature", "STD_REST_signature"):
    log = (run / f"logs/{name}.log").read_text(encoding="utf-8")
    method = re.search(r"^\[ERROR\] \S+\.(\w+Test\.\w+) -- .*<<< FAILURE!", log, re.M)
    if not method:
        raise SystemExit(f"Missing named architecture failure: {name}")
    print(method[1])
    if name == "STD_violation":
        print(next(line for line in log.splitlines() if line.startswith("Field <") and "__archViolation" in line))
