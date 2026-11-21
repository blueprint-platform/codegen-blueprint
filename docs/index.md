# Codegen Blueprint documentation

## Generate a project

- [Quickstart](quickstart.md) generates and verifies a runnable sample.
- [CLI reference](cli.md) lists options, dependency aliases and failure behavior.
- [Generated project examples](examples/generated-projects.md) show both layouts with Strict guardrails.

## Check what the build enforces

- [Architecture contract](architecture/executable-architecture-contract.md) explains the generated output, dependency directions and enforcement boundaries.
- [Guardrails rulebook](architecture/architecture-guardrails-rulebook.md) maps the modes to their ArchUnit assertions.
- [Executable proof](examples/boundary-proof.md) reproduces a compiling change that fails a named rule and passes after restoration.

## Plan an upgrade

- [Compatibility](compatibility.md) lists supported Java and Boot targets, bundled patches and verification coverage.
- [2.0 release notes](release-notes.md) explain the changes from 1.x.

## Maintain the project

- [Contributing](../CONTRIBUTING.md) covers changes to code, generated output and documentation.
- [Release readiness](maintainers/release-discipline.md) defines verification gates and release evidence.
