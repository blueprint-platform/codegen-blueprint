# Visual assets

| Purpose | Editable source | Output |
| --- | --- | --- |
| Executable boundary proof | [Proof SVG](../assets/proof-2.0.svg) | SVG, backed by the [recorded proof](../examples/proof-2.0.txt) |
| Architecture reference | [Dependency diagram SVG](../assets/architecture-2.0.svg) | SVG |
| GitHub link preview | [Social SVG](../assets/social-preview.svg) | [1280 × 640 PNG](../assets/social-preview.png) |

The proof graphic summarizes the standard controller-to-repository case. Update it only after rerunning the [proof](../examples/boundary-proof.md) and checking the corresponding verdict. Produce the portable record with `python3 scripts/proof-record.py path/to/proof-run > docs/examples/proof-2.0.txt`. The record keeps the artifact hash, JVM, resolved Boot patch and the failing rule names.

Export the social SVG at its declared 1280 × 640 size using a browser or vector renderer. Inspect it at full and half size. Uploading the PNG to repository settings is a maintainer action. [GitHub social preview instructions](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/customizing-your-repositorys-social-media-preview)
