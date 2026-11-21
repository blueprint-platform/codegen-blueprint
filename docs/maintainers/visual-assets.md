# Visual asset maintenance

Graphics should clarify a point that the surrounding prose does not already make. Keep detailed evidence, code and navigable links in Markdown rather than embedding them into a static image.

| Asset | Role | Technical source of truth |
| --- | --- | --- |
| [Proof PNG](../assets/architecture-proof.png) | Compact GREEN → RED → GREEN overview in the README | [Mutation and recorded results](../examples/boundary-proof.md) |
| [Architecture SVG](../assets/architecture.svg) | Compare selected dependencies in both layouts | [Guardrails rulebook](../architecture/architecture-guardrails-rulebook.md) |
| [Social preview SVG](../assets/social-preview.svg) and [PNG](../assets/social-preview.png) | Identify the project when a repository link is shared | Repository branding and sharing crop |

## Review checklist

- Inspect native-size and reduced-size renderings, including a 375 px viewport and both light/dark backgrounds.
- Verify that arrowheads point toward referenced code; prohibited dependencies must remain unambiguous.
- Keep the exact Java mutation, assertion and commands in the [proof document](../examples/boundary-proof.md), not in dense image microtext.
- Remove simulated hyperlinks, redundant headings and decorative text that repeats the page.
- For a changed proof graphic, rerun the named case and check every depicted result against evidence. Preserve historical JVM, patch, SHA and assertion details in their original record.

## Export the social preview

Export `social-preview.svg` to a **1280 × 640** PNG. From the repository root, set `CHROMIUM` to an installed Chromium executable:

```bash
"$CHROMIUM" --headless --disable-gpu --force-device-scale-factor=1 --hide-scrollbars   --user-data-dir="$(mktemp -d)" --window-size=1280,640   --screenshot="$PWD/docs/assets/social-preview.png"   "file://$PWD/docs/assets/social-preview.svg"
```

Compare the PNG against the SVG and inspect it as a thumbnail. Uploading a new [GitHub social preview](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/customizing-your-repositorys-social-media-preview) is a separate maintainer action.
