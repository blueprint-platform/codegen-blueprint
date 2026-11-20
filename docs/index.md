# Codegen Blueprint documentation

Generate a Spring Boot project, verify its architecture as part of the build, and reproduce a failure when a boundary is crossed.

## Start here

- [Quickstart](quickstart.md) builds the generator, creates a project and runs its verification.
- [Generated project walkthroughs](demo/generated-readmes.md) show both project layouts.
- [Executable proof](demo/executable-architecture-proof.md) shows the baseline, the failing ArchUnit assertion and the restored build.

## Reference

- [Compatibility](compatibility.md) specifies supported Java/Boot targets, reference patches and verification coverage.
- [CLI reference](cli.md) lists inputs, defaults, dependency aliases and patch resolution behavior.
- [Architecture contract](architecture/executable-architecture-contract.md) defines what generation delivers and what its tests enforce.
- [Guardrails rulebook](architecture/architecture-guardrails-rulebook.md) lists the rules for each layout and mode.

## Upgrade and maintain

- [Migrate to 2.0](guides/migrate-to-2.0.md)
- [2.0 release notes](releases/2.0.0.md)
- [Release readiness checklist](policies/release-discipline.md)
