# Architecture as an Executable Contract

Generate a Spring Boot project with Maven and make the intended dependency boundaries part of the build. Start with a working HTTP sample, cross one boundary, and see the exact architecture assertion that catches it.

[Generate a project](quickstart.md) · [Run the boundary proof](examples/boundary-proof.md)

## Understand the layout

Choose **standard** when controllers, services and repositories express your team's architecture. Choose **hexagonal** when you want inbound and outbound adapters to communicate through application ports, with business types in the domain and wiring in bootstrap.

The generator delivers the project and ZIP. The generated project's Maven lifecycle owns compilation, architecture checks and sample integration tests.

![Allowed and prohibited source dependencies for Standard Layered and Hexagonal projects](assets/architecture-2.0.svg)

## Generate and verify

Follow the [quickstart](quickstart.md) with JDK 25. Run the generated wrapper and look for the sample HTTP integration test and ArchUnit reports. The project targets **Boot 4.2 / Java 25** by default. Both Boot 4.1 and 4.2 can target Java 21 or 25. See [compatibility](compatibility.md) for the resolved patch and bundled-reference rules.

Start with `basic` for layout-specific checks, choose `strict` to retain Basic guarantees and add stronger rules, or use `none` for the layout without generated ArchUnit checks.

## Break a boundary

In the standard strict sample, let `GreetingController` declare a field of type `LoggingGreetingAuditRepository`. Java accepts the dependency. ArchUnit fails the rule that prohibits controllers from depending on repositories. Restore the controller and run the same Maven command again.

[![Baseline passes, the architecture rule fails, and restoration passes](assets/proof-2.0.svg)](examples/boundary-proof.md)

[Follow the proof steps](examples/boundary-proof.md) · [Read the exact rule failure](examples/proof-2.0.txt)

## Explore the contract

| Goal | Reference |
| --- | --- |
| Inspect what each layout delivers | [Generated project walkthroughs](examples/generated-projects.md) |
| Choose a layout, target or dependency | [CLI reference](cli.md) · [Compatibility](compatibility.md) |
| Check exactly what Maven enforces | [Architecture contract](architecture/executable-architecture-contract.md) · [Guardrails rulebook](architecture/architecture-guardrails-rulebook.md) |
| Review release scope and evidence | [Release notes](release-notes.md) · [Release readiness](maintainers/release-discipline.md) |
