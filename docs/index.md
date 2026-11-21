# Codegen Blueprint documentation

## Learn

- [Quickstart](quickstart.md) builds the generator, creates a sample project and runs its Maven wrapper.
- [Generated project walkthroughs](examples/generated-projects.md) show the Standard and Hexagonal outputs.
- [CLI reference](cli.md) covers options, dependency aliases and failure behavior.

## Understand

- [Executable architecture contract](architecture/executable-architecture-contract.md) describes what the generator delivers and what the build enforces.
- [Guardrails rulebook](architecture/architecture-guardrails-rulebook.md) maps each layout and enforcement mode to its ArchUnit assertions.

![Representative Standard and Hexagonal source dependencies, with solid permitted edges and dashed prohibited edges](assets/architecture-2.0.svg)

## Contribute

- [Contributing guide](../CONTRIBUTING.md) explains how to propose a change and verify it.
- [Documentation tooling](maintainers/documentation.md) covers local Pages builds, quickstart checks and generated snapshot checks.

## Trust the evidence

- [Boundary proof](examples/boundary-proof.md) reproduces a compiling violation, a named ArchUnit failure and a restored passing build.
- [Compatibility](compatibility.md) distinguishes supported targets, verified patches and bundled references.
- [Release notes](release-notes.md) describe the unreleased 2.0 changes. [Release readiness](maintainers/release-discipline.md) lists the verification gates.
