# Visual assets

| Asset | Purpose | Review against |
| --- | --- | --- |
| [README proof PNG](../assets/architecture-proof.png) | Shows the injected Java dependency and the three observed verdicts | [Proof procedure](../examples/boundary-proof.md) and [historical run](../examples/proof-2.0.txt) |
| [Architecture SVG](../assets/architecture.svg) | Shows selected source dependencies in both layouts | [Guardrails rulebook](../architecture/architecture-guardrails-rulebook.md) |
| [Social preview SVG](../assets/social-preview.svg) and [PNG](../assets/social-preview.png) | Identifies the project when a repository link is shared | Repository social preview crop |

Review images at native and reduced sizes on light and dark backgrounds. At a 375 px viewport, check that the proof code and verdicts are legible. Keep the exact assertion and full reproduction commands in the proof document. In the architecture diagram, arrows point to referenced code. Red labels identify prohibited edges.

When updating the proof image, rerun the corresponding case and compare the displayed mutation and verdicts with the new evidence. To format a full run as a portable result, use `python3 scripts/proof-record.py path/to/proof-run`. Retain the historical record with its original artifact hash, JVM, resolved patch and rule names.

Export the social SVG at 1280 × 640. Set `CHROMIUM` to the executable path and run from the repository root.

```bash
"$CHROMIUM" --headless --disable-gpu --force-device-scale-factor=1 --hide-scrollbars \
  --user-data-dir="$(mktemp -d)" --window-size=1280,640 \
  --screenshot="$PWD/docs/assets/social-preview.png" \
  "file://$PWD/docs/assets/social-preview.svg"
```

Check the PNG against the SVG and inspect a thumbnail. Uploading it in repository settings is a maintainer action. [GitHub social preview instructions](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/customizing-your-repositorys-social-media-preview)
