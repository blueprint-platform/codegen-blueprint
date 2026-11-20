# Executable architecture contract

Codegen Blueprint 2.0 delivers a Spring Boot project directory, a sibling ZIP and the architecture tests for the chosen layout and mode. The generated project owns its build and subsequent lifecycle.

![The CLI validates a blueprint, resolves a concrete Boot patch and publishes a Maven project whose build runs ArchUnit](../assets/architecture-2.0.svg)

## Guarantees

- **Standard** produces layered package families. **Hexagonal** produces ports-and-adapters families. Both use conventional Maven source and test roots.
- `basic` is the default. `strict` applies a layout-specific set of additional rules. `none` omits ArchUnit tests and the ArchUnit dependency. [Guardrails rulebook](architecture-guardrails-rulebook.md)
- `./mvnw clean verify` evaluates applicable ArchUnit rules against compiled classes. Sample-enabled output also executes the generated integration tests. These tests are delivered with the project and need no generator installation.
- The generated tree and file contents repeat for the same generator, normalized inputs and resolved patch. ZIP byte identity is outside the contract.
- The generator protects existing outputs and attempts to clean up files it owns on normal errors. It reports uncertainty or leftover paths. The directory and ZIP are separate filesystem operations, with no cross-path atomicity guarantee on interruption or crash.

## Boundaries

The build enforces rules on imported classes matching its scope. The guardrails do not certify application behavior, runtime architecture, production security or every possible layering policy. After delivery, application design, code changes, dependencies and deployment belong to the consumer.

The [compatibility reference](../compatibility.md) defines platform support. The [proof](../examples/boundary-proof.md) shows which violations were executed.
