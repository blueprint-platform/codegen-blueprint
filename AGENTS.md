# Codegen Blueprint repository contract

## Authority and boundaries

- The human architect owns architectural intent and merge decisions. Agents draft implementation and gather proof. For non-trivial changes record intent, boundary, contract, executable proof, and drift check. See [Architecture Governance & AI Protocol](docs/architecture/architecture-governance-and-ai-protocol.md).
- Use [the approved 2.0 architecture design](CODEGEN_BLUEPRINT_2_0_0_ARCHITECTURE_DESIGN.md) for the active implementation and acceptance gates. The [architecture contract](docs/architecture/executable-architecture-contract.md) defines current guarantees. Apply [Release Discipline](docs/policies/release-discipline.md).
- This is one Maven module. `domain` owns blueprint, value objects, validation and compatibility. Keep it framework-free and free of IO implementations. Resource and writer port signatures use JDK value types. `application` depends on domain and owns command/result contracts, use-case sequencing, artifact selection/generation, ZIP and output discovery ports. Keep Spring, Picocli, FreeMarker and filesystem implementation out of core.
- CLI DTOs enter through `CreateProjectCommandMapper` and `CreateProjectPort`. Keep inbound adapters off outbound ports and core implementations. Outbound adapters implement ports and own technology-specific models. Bootstrap assembles them and owns Spring/Picocli wiring.
- Use the existing seam for the change. Keep edits scoped and reviewable. New architectural concepts, public API renames and contract changes need a human decision. Add abstractions when a concrete requirement justifies the additional indirection.

## Generation and compatibility

- `application.yml` owns artifact selection and order for `spring-boot-maven-java`. Bootstrap binds definitions and key-to-port mappings. Artifact adapters own applicability and model/output derivation. Templates own rendered syntax. For an artifact change check key, registry, profile, template paths and generated outputs together.
- Preserve the 2.0 CLI and generated contracts. The generator compiles for Java 21 and runs on Java 21/25, with Boot 4.1.3 owned by the root POM. Generated Boot lines are 4.1/4.2 and targets Java 21/25. Defaults are Boot 4.2 and Java 25. Preserve stable patch discovery and explicit bundled fallback reporting.
- Resolve CLI aliases in `SpringBootDependencyOption`. Keep normalization/duplicate policy in domain and the effective Maven dependency plan in the profile adapter. Check implicit MVC, JPA H2 and ArchUnit. POM and strict REST guardrails consume the same effective MVC decision.
- Preserve ordered materialization and UTF-8. Publish through the staging and exclusive-claim boundary. Preserve existing outputs and report identity/cleanup uncertainty. Compare generated trees and content for determinism. ZIP byte identity and executable permission require separate evidence.

## Guardrails and proof

- Preserve `none` (no generated guardrails or ArchUnit dependency), default `basic`, and layout-specific `strict`. Keep scope sanity, canonical package schema and anchor drift checks aligned with layouts and samples. Preserve legitimate empty optional scopes and check selector coverage before changing `allowEmptyShould`. Build sample-free output as well as populated samples. For changed rules confirm a compiling violation fails at the intended ArchUnit assertion.
- From repository root on Java 21, run `mvn -B -ntp test` for Surefire or `mvn -B -ntp clean verify` for Surefire, engine ArchUnit, Failsafe and the executable JAR. Root CLI E2E uses the real executor in-process. Root integration tests establish wiring, generation and CLI/result behavior.
- After root verify, use `bash ci/verify-gen-hex-guardrails-strict-sample-basic.sh` or the affected variant. Set `BOOT_VERSION=4.1` or `4.2` and `JAVA_VERSION=21` or `25` with the matching build JDK. The scripts verify the packaged JAR output, then run the generated wrapper and check ArchUnit and sample Failsafe reports.
- For the negative demo run `bash docs/demo/proof/proof-runner.sh` after packaging. Confirm the expected ArchUnit report failure and restored green build. Separate local results, configured CI and observed remote executions.

## Documentation and publication

- Treat compatibility, CLI reference and the architecture contract as the authorities for user-facing docs. Build Pages and execute the canonical quickstart as described in the [documentation tooling guide](docs/site/README.md).

- Update the affected README, rulebook or contract documentation when behavior changes. Review generated README template and tracked demo README snapshots together for intentional output changes. Keep transient projects, ZIPs and proof logs out of commits. Internal templates are implementation details and generated output is the public review surface.
- Treat version bumps, release tags, binaries and public publication as explicit maintainer decisions. Use local verification by default. Obtain explicit authorization for release/publication actions and verify version, artifact and contract alignment before publishing.
