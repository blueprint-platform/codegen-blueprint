# Architecture your build can check

Codegen Blueprint turns a layout and a guardrail policy into a Spring Boot Maven project. From the first commit, the project's own build can show which dependency crossed an architectural boundary.

[Generate a service](quickstart.md) · [Read the build proof](examples/boundary-proof.md)

## A failure you can explain

![A new controller field creates a direct dependency on the concrete repository alongside the existing controller-to-service relationship.](assets/controller-boundary.svg)

```text
PASS: STD baseline
EXPECTED_FAIL: STD violation
PASS: STD fixed
```

These are the runner’s Standard verdicts from the recorded 2.0 run. The red build compiles the new field and fails the generated dependency assertion. Follow the [walkthrough](examples/boundary-proof.md#standard-controller-depends-on-a-repository) to see the source change and the exact report.

## Generate and use a project

- [Quickstart](quickstart.md) builds the generator and verifies a runnable Hexagonal service.
- [CLI reference](cli.md) covers accepted options, validation, patch selection and retry behavior.
- [Generated project examples](examples/generated-projects.md) compare how the same greeting is structured in the two layouts.

## Understand the contract

- [Executable architecture contract](architecture/executable-architecture-contract.md) explains what the generator delivers and where its guarantees end.
- [Guardrails rulebook](architecture/architecture-guardrails-rulebook.md) maps the layouts and modes to the emitted checks.
- [Generator architecture](guides/how-to-explore-hexagonal-architecture.md) follows a request through validation, ports, adapters and publication.
- [Scope and responsibility](policies/scope-and-responsibility.md) explains which decisions stay with the consuming team.

## Verify a target or an upgrade

- [Compatibility](compatibility.md) distinguishes supported targets, bundled references, live candidates and tested minimum versions.
- [2.0 release notes](release-notes.md) give a migration path for generation recipes from 1.x.

## Maintain Codegen Blueprint

- [Architecture governance](maintainers/architecture-governance-and-ai-protocol.md) explains ownership and evidence for a contract change.
- [Release readiness](maintainers/release-discipline.md) describes matrix coverage and release gates.
- [Documentation tooling](maintainers/documentation.md) covers Pages previews and snapshot drift checks.
- [Visual assets](maintainers/visual-assets.md) records how to maintain the diagrams and historical proof.
- [Contributing](../CONTRIBUTING.md) covers development and review.
