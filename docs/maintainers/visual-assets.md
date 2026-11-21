# Visual assets

| Purpose | Editable source | Output |
| --- | --- | --- |
| Executable boundary proof | [Proof SVG](../assets/proof-2.0.svg) | SVG, backed by the [recorded proof](../examples/proof-2.0.txt) |
| Architecture reference | [Dependency diagram SVG](../assets/architecture-2.0.svg) | SVG |
| GitHub link preview | [Social SVG](../assets/social-preview.svg) | [1280 × 640 PNG](../assets/social-preview.png) |

The proof graphic summarizes the standard controller-to-repository case. Update it only after rerunning the [proof](../examples/boundary-proof.md) and checking the corresponding verdict. Produce the portable record with `python3 scripts/proof-record.py path/to/proof-run > docs/examples/proof-2.0.txt`. The record keeps the artifact hash, JVM, resolved Boot patch and the failing rule names.

Treat the SVGs as the editable sources. Review them at native and half size on light and dark backgrounds. Check text fit, contrast, arrow direction and accessible titles and descriptions. Solid edges indicate permitted dependencies and dashed edges indicate prohibited ones.

Export the social PNG from the SVG at 1280 × 640. For example, set `CHROMIUM` to the path of a Chromium or Chrome executable and run from the repository root.

```bash
"$CHROMIUM" --headless --disable-gpu --force-device-scale-factor=1 --hide-scrollbars \
  --user-data-dir="$(mktemp -d)" --window-size=1280,640 \
  --screenshot="$PWD/docs/assets/social-preview.png" \
  "file://$PWD/docs/assets/social-preview.svg"
```

Check the exported dimensions and compare the PNG with the SVG at full size and in a thumbnail or crop. Uploading the PNG to repository settings is a maintainer action. [GitHub social preview instructions](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/customizing-your-repositorys-social-media-preview)
