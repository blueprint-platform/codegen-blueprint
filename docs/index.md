# Documentation

Start with a runnable project, then explore the architecture checks or the 2.0 migration. Reference material is separated from the hands-on walkthroughs so each page has one job.

## Start building

| Guide | What you'll find |
| --- | --- |
| [Quickstart](quickstart.md) | Generate a Hexagonal project and verify it with its Maven wrapper |
| [CLI reference](cli.md) | Selectors, dependency aliases, version resolution and failures |
| [Generated walkthroughs](examples/generated-projects.md) | The generator's Standard and Hexagonal README snapshots |

## Understand the architecture

| Reference | What you'll find |
| --- | --- |
| [Executable architecture contract](architecture/executable-architecture-contract.md) | Output guarantees, dependency directions and verification boundaries |
| [Guardrails rulebook](architecture/architecture-guardrails-rulebook.md) | Exact Basic and Strict assertions and their scope |
| [Boundary proof](examples/boundary-proof.md) | A compiling violation, the named ArchUnit failure and recovery |

## Upgrade and maintain

- [Compatibility](compatibility.md) — supported Boot/Java combinations and verification coverage.
- [2.0 release notes](release-notes.md) — migration changes from 1.x.
- [Contributing](../CONTRIBUTING.md) — development and verification expectations.
- [Release readiness](maintainers/release-discipline.md) — CI matrices and release checks.

Return to the [project overview](../README.md).
