#!/usr/bin/env python3
"""Execute the fenced commands from the published quickstart against a packaged JAR."""

import os
from pathlib import Path
import re
import subprocess
import sys
from tempfile import TemporaryDirectory

ROOT = Path(__file__).resolve().parents[1]
source = (ROOT / "docs/quickstart.md").read_text(encoding="utf-8")
examples = re.findall(r"<!-- quickstart:verified -->\n```bash\n(.*?)\n```\n"
                      r"<!-- /quickstart:verified -->", source, flags=re.S)
if len(examples) != 1:
    raise SystemExit("Expected exactly one verified quickstart block")
jar = Path(sys.argv[1]).resolve(strict=True)
with TemporaryDirectory(prefix="codegen-quickstart-") as work:
    env = {**os.environ, "CODEGEN_JAR": str(jar), "TMPDIR": work}
    subprocess.run(
        ["bash", "-euo", "pipefail", "-c", examples[0] + """
test -f "$OUTPUT_DIR/greeting.zip"
grep -F '<java.version>25</java.version>' pom.xml
for suite in GreetingSampleHttpIT HexagonalStrictRestBoundarySignatureIsolationTest; do
  report="$(find target/surefire-reports target/failsafe-reports -name "TEST-*.$suite.xml" -print -quit)"
  test -f "$report"
  grep -El 'tests="[1-9][0-9]*"' "$report"
done
"""], cwd=work, env=env, check=True)
print("Verified the documented quickstart, sample HTTP and strict ArchUnit reports")
