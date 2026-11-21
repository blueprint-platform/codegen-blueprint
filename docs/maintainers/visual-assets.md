# Visual asset maintenance

| Asset | Purpose | Review against |
| --- | --- | --- |
| [README cover](../images/cover/cover.png) and [HTML source](../images/cover/cover.html) | Introduce the generator and show a CLI example in the original two-panel cover | [Quickstart](../quickstart.md) and [compatibility](../compatibility.md) |
| [Original value proposition diagram](../images/architecture/value-proposition.png) and [Draw.io source](../diagrams/value-proposition.drawio) | Explain who uses the engine and how profiles become project output | [Documentation index](../index.md) |
| [2.0 Surefire excerpt](../images/architecture/standard-strict-proof.png) | Show the failing assertion and field from a real proof run | [2.0 replay record](../examples/boundary-verification-restoration.txt) |
| [Historical terminal capture](../demo/images/mini-standard-controller-repository-failure.png) | Preserve the earlier 1.x example with its version context | [Historical capture in the proof](../examples/boundary-proof.md#historical-terminal-capture) |
| [Architecture diagram](../assets/architecture.svg) | Compare selected source dependencies in both layouts | [Guardrails rulebook](../architecture/architecture-guardrails-rulebook.md) |

Check the assets at their intended display sizes, including a 375 px viewport on light and dark backgrounds. Arrowheads point toward referenced code. Keep the exact Java mutation, assertion and reproduction commands in the [proof walkthrough](../examples/boundary-proof.md). Review proof wording against the recorded outputs before updating it.

The cover HTML uses a 1200 × 630 preview canvas. When updating the PNG, capture the 1100 × 540 frame at 2× with Chromium. Review the exported image at the README’s 700 px display width. GitHub’s repository sharing image is managed separately in repository settings.

The 2.0 excerpt uses the assertion line, rule description and field dependency from `excerpts/STD_violation.excerpt.txt`. A future refresh should come from a completed proof and preserve the actual log text, with the run's JDK, Boot patch and date in the caption. Keep the accessible text excerpt beside the image in the walkthrough.
