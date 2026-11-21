# Scope and responsibility

Codegen Blueprint delivers a starting Maven project and the selected architecture checks. After generation, the project belongs to the consuming team. Commit the generated output, review dependency updates and keep the architecture tests in the normal build to carry the contract forward.

## What a green build establishes

| Evidence | Scope |
| --- | --- |
| Generated ArchUnit tests | Named package and dependency constraints on selected compiled production types |
| Spring Boot context test | The generated application context starts in the tested configuration |
| Sample HTTP tests | The greeting routes respond as asserted using a real server in the test environment |
| Compatibility matrix | Selected scenarios built with the specified Boot patches, Maven wrapper and target JDKs |

Production performance, resilience, availability, security policies and deployment configuration require evidence from the consuming application. ArchUnit inspects bytecode structure. Application behavior still needs tests of business rules, transactions and runtime wiring. [Executable contract](../architecture/executable-architecture-contract.md)

## Where to adapt the starting point

- **Organization-specific architecture.** Standard and Hexagonal encode two predefined package models. Review the exact selectors and add tests for your team's additional policies. When relocating canonical packages, update and exercise the corresponding tests as part of the change. [Rulebook](../architecture/architecture-guardrails-rulebook.md)
- **Dependencies.** The CLI accepts a controlled alias set. Add your own dependencies and BOM decisions in the generated project. Dependency aliases select libraries. A security starter, for example, gives you Spring Security dependencies, while production authorization remains a consuming application policy. [CLI reference](../cli.md#dependency-aliases)
- **Shared behavior.** Central ownership and versioned libraries let teams change security, logging and observability policy without editing copies of generated implementations. The greeting sample illustrates a local audit side effect and HTTP transport, with deployment and platform integration left to the consuming team.
- **Upgrades.** Generate a separate candidate and compare it with your project's sources and build. Existing services have their own lifecycle, and internal FreeMarker templates and adapter classes are implementation details. The CLI delivers a new project and ZIP. [Migration guidance](../release-notes.md)
