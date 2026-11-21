# Visual assets

| Purpose | Editable source | Output |
| --- | --- | --- |
| Executable boundary proof | [Proof SVG](../assets/architecture-proof.svg) | SVG, backed by the [recorded proof](../examples/proof-2.0.txt) |
| Architecture reference | [Dependency diagram SVG](../assets/architecture.svg) | SVG |
| README cover and GitHub link preview | [Social SVG](../assets/social-preview.svg) | [1280 × 640 PNG](../assets/social-preview.png) |

The proof graphic summarizes the standard controller-to-repository case. Update it only after rerunning the [proof](../examples/boundary-proof.md) and checking the corresponding verdict. Print a fresh portable result with `python3 scripts/proof-record.py path/to/proof-run`. The historical record keeps the artifact hash, JVM, resolved Boot patch and the failing rule names.

The architecture and proof SVGs contain editable artwork. The social SVG embeds the crop of the supplied cover artwork, so it renders without an external image. The architecture diagram compares source dependencies, the proof shows a tested failure and recovery, and the social card introduces the project. Review them at native and half size on light and dark backgrounds. Check both README diagrams at a 375 px viewport. Check text fit, contrast, arrow direction and accessible titles and descriptions. Teal arrows connect permitted source dependencies. Red callouts identify selected prohibited dependencies.

Export the social PNG from the SVG at 1280 × 640. For example, set `CHROMIUM` to the path of a Chromium or Chrome executable and run from the repository root.

```bash
"$CHROMIUM" --headless --disable-gpu --force-device-scale-factor=1 --hide-scrollbars \
  --user-data-dir="$(mktemp -d)" --window-size=1280,640 \
  --screenshot="$PWD/docs/assets/social-preview.png" \
  "file://$PWD/docs/assets/social-preview.svg"
```

Check the exported dimensions and compare the PNG with the SVG at full size and in a thumbnail or crop. Uploading the PNG to repository settings is a maintainer action. [GitHub social preview instructions](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/customizing-your-repositorys-social-media-preview)
