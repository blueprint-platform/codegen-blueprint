# Visual assets

| Purpose | Editable source | Output |
| --- | --- | --- |
| Repository proof strip | [Proof SVG](proof-2.0.svg) | SVG, backed by the [recorded proof](../demo/proof-2.0.txt) |
| Architecture reference | [Flow SVG](architecture-2.0.svg) | SVG |
| GitHub link preview | [Social SVG](social-preview.svg) | [1280 × 640 PNG](social-preview.png) |

The proof strip summarizes the standard controller-to-repository case. Update it only after rerunning the [proof](../demo/executable-architecture-proof.md) and checking the corresponding verdict. Produce the portable record with `python3 scripts/proof-record.py path/to/proof-run > docs/demo/proof-2.0.txt`. The record keeps the artifact hash, JVM, resolved Boot patch and the failing rule names.

Export the social SVG at its declared 1280 × 640 size using a browser or vector renderer. Inspect it at full and half size. Uploading the PNG to repository settings is a maintainer action. [GitHub social preview instructions](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/customizing-your-repositorys-social-media-preview)

The active tree contains the sources and outputs used for 2.0. The original cover, diagrams and proof screenshots are available in the [v1.0.3 documentation tree](https://github.com/blueprint-platform/codegen-blueprint/tree/v1.0.3/docs).
