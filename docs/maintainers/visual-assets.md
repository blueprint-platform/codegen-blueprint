# Visual asset maintenance

The documentation uses a small set of technical figures. The same source-dependency diagram is rendered on GitHub and Pages. The build proof presents the assertion as selectable text, with the original visual records available for inspection.

| Asset | Source and review |
| --- | --- |
| [Controller boundary diagram](../assets/controller-boundary.svg) | Shows the original service path and the field dependency introduced by the Standard proof. Check its two destinations against the [source mutation](../examples/boundary-proof.md#standard-controller-depends-on-a-repository). |
| [Source-dependency diagram](../assets/architecture.svg) | Edit the SVG directly. Arrows point from dependent code toward the type it references. Check the paths against the [generated layout contract](../architecture/executable-architecture-contract.md#choose-a-layout). |
| [2.0 Surefire excerpt](../images/architecture/standard-strict-proof.png) | Uses the assertion line, rule description and field dependency from `excerpts/STD_violation.excerpt.txt`. The [run record](../examples/boundary-verification-restoration.txt) identifies the generator and environment. |
| [1.x terminal capture](../demo/images/mini-standard-controller-repository-failure.png) | Historical evidence displayed with its release context in the [proof walkthrough](../examples/boundary-proof.md#historical-terminal-capture). |

Review figures at a 375 px viewport and at desktop size with both theme preferences. Keep a text equivalent beside proof images and a descriptive alternative for diagrams. Page typography, spacing and theme colors are defined in [the Pages stylesheet](../../scripts/pages/site.css).
