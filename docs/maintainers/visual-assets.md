# Visual asset maintenance

| Asset | Purpose | Review against |
| --- | --- | --- |
| [README cover](../images/cover/cover.png) and [HTML source](../images/cover/cover.html) | Introduce the generator and show a CLI example in the original two-panel cover | [Quickstart](../quickstart.md) and [compatibility](../compatibility.md) |
| [Architecture diagram](../assets/architecture.svg) | Compare selected source dependencies in both layouts | [Guardrails rulebook](../architecture/architecture-guardrails-rulebook.md) |

Check the assets at their intended display sizes, including a 375 px viewport on light and dark backgrounds. Arrowheads point toward referenced code. Keep the exact Java mutation, assertion and reproduction commands in the [proof walkthrough](../examples/boundary-proof.md). Review proof wording against the recorded outputs before updating it.

The cover HTML uses a 1200 × 630 preview canvas. When updating the PNG, capture the 1100 × 540 frame at 2× with Chromium. Review the exported image at the README’s 700 px display width. GitHub’s repository sharing image is managed separately in repository settings.
