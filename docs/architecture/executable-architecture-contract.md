# Executable architecture contract

Codegen Blueprint delivers a Spring Boot Maven project directory and a sibling ZIP. A blueprint selects the project structure, dependency inputs and architecture policy. The generated project runs its own tests and owns its later lifecycle.

- [From blueprint to build](#from-blueprint-to-build)
- [Generated project and ZIP](#generated-project-and-zip)
- [Layout dependencies](#choose-a-layout)
- [What the evidence means](#what-the-evidence-means)
- [Delivery and reproducibility](#delivery-and-reproducibility)

## From blueprint to build

An architecture decision survives handoff when a new contributor can see where a responsibility goes, make a change, and get an actionable build result. The blueprint captures the requested architecture and stack. The profile turns that intent into ordered artifacts. The project's own Maven build evaluates the emitted rules after compilation. These three steps let the team review generation inputs and enforcement independently.

The CLI maps options to a generation command. The application validates the request, chooses a profile and resolves the target through a port. It then coordinates rendering and publication. The generated source tree, POM, wrapper and selected tests capture those decisions.

Run `sh ./mvnw clean verify` inside the generated project. Surefire runs ArchUnit against the compiled classes and writes XML under `target/surefire-reports/`. If the sample is enabled, Failsafe runs separate integration tests, including real HTTP. The [quickstart](../quickstart.md) shows both report locations.

The domain defines target compatibility and requires a resolved target in the blueprint. The selected generation profile checks its metadata constraints before the resolver can access the network. The Spring Boot outbound adapter owns Central lookup and bundled fallback, with references supplied by bootstrap. The CLI maps the generic resolution source to its public labels and renders fallback warnings from message keys.

Bootstrap validates artifact order and fixed template definitions at startup. The sample and guardrails adapters select and scan templates for the requested layout and mode during generation.

Engine ArchUnit tests enforce the application dependency boundary. Use case and CLI integration tests check that invalid metadata stops before resolution, and adapter tests verify which lookup failures permit fallback.

## Generated project and ZIP

The single-module Maven project includes a POM with the exact Boot parent and target Java version, Maven wrapper scripts and distribution properties, a Spring Boot main class and context test, application YAML, a Git ignore file, and a README. The source layout emits the canonical family directories and top-level package markers, including when the sample is disabled. Classes for the optional greeting slice live under `bp.sample`. The sample adds both HTTP code and integration tests. Enabled guardrails generate Java sources under `src/test/java/<package>/architecture/archunit/`.

The wrapper invokes Maven 3.9.16. The generated build still needs its distribution and dependencies from an accessible repository or cache. The [generated READMEs](../examples/generated-projects.md) show what the next maintainer receives with each project.

## Choose a layout

Arrows in the diagram point from dependent code to the code it references. The red lines of text identify selected dependencies that both Basic and Strict prohibit.

![Standard allows controller to service to repository. The Hexagonal handler and inbound adapter reference an application input port. The handler and outbound adapter reference a domain output port.](../assets/architecture.svg)

In **Standard**, each feature uses layered packages, with component wiring in configuration. This follows [Fowler’s presentation, domain and data layering](https://martinfowler.com/bliki/PresentationDomainDataLayering.html).

In **Hexagonal**, a use case calls an outbound port at runtime. The driven adapter implements that port and depends on its interface in source code; bootstrap wires the implementation into the use case. This follows [Cockburn’s Ports and Adapters](https://alistair.cockburn.us/hexagonal-architecture/).

The diagram omits assembly and other sample dependencies. In Standard, configuration constructs the service and repository. In Hexagonal, the composition root can name concrete implementations and supply them to the handler. In both layouts, the transport maps application results into HTTP response types. These choices keep framework-specific assembly and response shape out of the business operation.

Standard keeps a direct service-to-repository dependency available and uses package rules to prevent common shortcuts. It fits a team that wants the familiar layered model and separate orchestration and transport. Hexagonal exposes an application input API and keeps concrete technology decisions behind adapters. In the generated sample, `GetGreetingPort` belongs to `application.port.in` and `GreetingAuditPort` belongs to `domain.port.out`. It makes substitutions possible at the composition root at the cost of more interfaces and explicit mapping in the sample.

The [guardrails rulebook](architecture-guardrails-rulebook.md) defines what each mode enforces. Strict retains Basic guarantees and may report a violation under a different assertion.

## What the evidence means

ArchUnit inspects compiled types imported from the generated base package and applies each rule's selectors. A field type, implemented interface or method signature can create a dependency even if the code never runs. Test classes are excluded from the architecture import. The generated rules allow selected optional scopes to be absent, so an unused adapter family or an application without MVC can still build. Package names organize the check; they alone provide no evidence that every business class was imported or correctly classified. [ArchUnit JUnit and import guide](https://www.archunit.org/userguide/html/000_Index.html)

A passing architecture test establishes the named constraint on the selected bytecode. The [boundary proof](../examples/boundary-proof.md) shows the corresponding evidence for a failure and recovery. Application correctness, dynamic wiring and runtime security need their own evidence. The consumer owns application code, dependency updates, deployment and any added architecture policies.

For evidence about the engine itself, follow the [Hexagonal engine walkthrough](../guides/how-to-explore-hexagonal-architecture.md). For evidence still owned by the generated application's team, see [scope and responsibility](../policies/scope-and-responsibility.md).

## Delivery and reproducibility

Use [compatibility](../compatibility.md) and the [CLI reference](../cli.md) for generator JVMs, target JDKs and Boot patch selection. With the same generator, normalized inputs and resolved patch, generation preserves the project tree and file contents. ZIP byte identity has no guarantee.

Before staging, the publisher validates the ordered manifest and rejects file collisions and paths that descend through another file. It writes the tree and builds the ZIP in a private staging directory, then claims the destination directory, files and ZIP without overwriting existing entries. Rollback follows reverse creation order and the CLI can report cleanup uncertainty alongside the original failure.

If filesystem identity is missing or changes, staging verification fails or rollback leaves the uncertain path in place. Keep the target and staging tree free from concurrent edits. Portable path operations leave a race between identity checks and access. The final directory and ZIP may be visible before publication finishes. A process interruption or crash can leave either output incomplete.
