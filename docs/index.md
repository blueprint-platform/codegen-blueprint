# Codegen Blueprint documentation

## Generate a project

- [Quickstart](quickstart.md) walks through generation, wrapper verification and test reports.
- [CLI reference](cli.md) covers options, dependency aliases and failure behavior.
- [Generated walkthroughs](examples/generated-projects.md) show the Standard and Hexagonal outputs.

## Understand and verify the boundaries

- [Executable architecture contract](architecture/executable-architecture-contract.md) defines what the generator delivers and what the build enforces.
- [Guardrails rulebook](architecture/architecture-guardrails-rulebook.md) maps each layout and mode to its ArchUnit assertions.
- [Boundary proof](examples/boundary-proof.md) shows the actual Java change and Surefire failure, then gives commands to reproduce the single example or all five scenarios.

## Check compatibility or contribute

- [Compatibility](compatibility.md) distinguishes supported targets, verified patches and bundled references.
- [Release notes](release-notes.md) cover the unreleased 2.0 changes. [Release readiness](maintainers/release-discipline.md) describes the verification gates.
- [Contributing guide](../CONTRIBUTING.md) explains how to propose and verify a change. See [documentation tooling](maintainers/documentation.md) for Pages, quickstart and snapshot checks.
