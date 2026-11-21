# Codegen Blueprint operating contract

Preserve **Architecture as an Executable Contract**. Codegen Blueprint delivers a project and ZIP. The generated project's Maven build enforces the selected architecture rules.

## Authority and ownership

The human architect owns product scope, architectural intent and publication. Use source, build configuration and executed tests to establish current behavior. Reconcile user-facing claims with the [architecture contract](docs/architecture/executable-architecture-contract.md), [CLI reference](docs/cli.md) and [compatibility reference](docs/compatibility.md). For consequential changes, record the intent, affected boundary, observable contract and executable proof. Follow the [governance protocol](docs/maintainers/architecture-governance-and-ai-protocol.md).

| Area | Owns |
| --- | --- |
| `domain` | `ProjectBlueprint`, value objects, invariants and compatibility policy. Pure Java, without framework or IO implementations. |
| `application` | Use-case ordering and ports. `CreateProjectHandler` validates the request, selects the artifact profile, and coordinates target resolution, generation and publication through framework-independent ports. |
| `adapter.in` | Picocli options and aliases. Map requests through `CreateProjectCommandMapper` to `CreateProjectPort`. |
| `adapter.out` | Platform-specific resolution and Central transport, profile-specific generation, template rendering and filesystem publication. |
| `bootstrap` | Spring/Picocli wiring and profile bindings. Keep framework types and adapter implementations out of core. |

Use an existing seam before adding one. Follow the complete change path from the input or registry to the generated output and the test that exercises it.

## Contracts that cross layers

- **Versions.** The root [POM](pom.xml) owns the generator's Spring Boot 4.2.0 runtime and Java 25 compile target. Build and run the generator on JDK 25. Public Boot selectors are `4.1` and `4.2`, with generated defaults Boot 4.2 and Java 25. The generated POM pins the resolved exact patch. Keep supported lines, Central latest, bundled verified references and tested minimum edges distinct. Eligible connection failures produce visible `BUNDLED_FALLBACK`. Bad metadata and invalid or missing parents fail explicitly. A fallback selects a generation patch; Maven still needs its distribution and dependencies.
- **Artifacts and templates.** [Profile configuration](src/main/resources/application.yml) owns artifact selection and order. Bootstrap maps keys to implementations. Adapters own applicability and model construction. FreeMarker templates own rendered syntax. Keep the effective dependency decision in the Maven profile so the POM and strict REST rules agree when a sample implicitly enables MVC.
- **Guardrails.** Preserve `none`, default `basic` and layout-specific `strict` for both standard and hexagonal. Keep package schema, scope and anchor checks aligned with actual generated classes. Allow empty scopes only where the feature is optional. For a changed rule, prove that a compilable violation fails at the named ArchUnit assertion and that restoration passes. See the [rulebook](docs/architecture/architecture-guardrails-rulebook.md).
- **Output and failure.** Preserve ordered UTF-8 generation, staging, exclusive publication and ownership-aware cleanup. Protect both existing output paths and report cleanup uncertainty. Compare generated trees and file contents for determinism. ZIP byte identity and crash-atomic publication are outside that guarantee. Preserve the generated wrapper, Failsafe discovery and Maven-resolved Mockito agent.

## Verification

Run from the repository root. Use Maven 3.9.16 as the engine reference.

| Change surface | Command and evidence |
| --- | --- |
| Engine logic | `mvn -B -ntp test` runs Surefire, including engine ArchUnit. |
| Wiring, packaging or runtime | `mvn -B -ntp clean verify` runs engine tests, Failsafe and JAR packaging. Run the engine on JDK 25 and verify generated output on the affected target JDK. Engine CLI integration tests exercise the executor in-process. |
| Generated output | `BOOT_VERSION=4.2 JAVA_VERSION=25 bash ci/verify-generated.sh standard` uses a packaged JAR, generated wrapper and test reports. Select the affected cell and its build JDK. Use `CODEGEN_JAR` to reuse the artifact across JDKs. |
| Negative architecture proof | `PROOF_MODE=standard bash ci/proof/proof-runner.sh` verifies the controller-to-repository case. Omit `PROOF_MODE` to run all five cases. Compilation and download failures do not establish a rule failure. |
| Docs and generated README | Follow the [docs tooling guide](docs/maintainers/documentation.md) for the Pages build, actual quickstart and snapshot drift checks. |

Use the [release checklist](docs/maintainers/release-discipline.md) for the four-cell Standard matrix, Full release checks and scheduled Central candidates. Report which commands ran, the relevant JDK and Boot patch, and any gaps. Distinguish local evidence from observed hosted CI.

## Repository hygiene and publication

Keep generated examples only where they serve as reviewed verification artifacts. Regenerate the [tracked README examples](docs/examples/generated-projects.md) when changing their template. Keep transient projects, ZIPs and raw logs out of commits. Repository guidance must work in a clean checkout. Link only to tracked local files, review the staged list, and leave unrelated and untracked files alone. Version promotion, tags, publication and deployment require explicit maintainer authorization.
