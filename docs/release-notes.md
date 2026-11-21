# Codegen Blueprint 2.0.0 — release notes

**Status:** `2.0.0-SNAPSHOT` on the development branch. The 2.0.0 binary has not been published.

Version 1.x already shipped Standard and Hexagonal layouts with executable architecture guardrails. **2.0 changes the supported generation contract**, not the original guardrails concept.

## Breaking changes from 1.x

| Area | 2.0 contract |
| --- | --- |
| Generator runtime | JDK 25; Spring Boot 4.2.0 internally |
| Generated Spring Boot | 4.1.x or 4.2.x; Boot 3.x and 4.0 are not supported targets |
| Generated Java | 21 or 25; default 25 |
| Boot selection | Pass a minor line (`--boot 4.1` or `--boot 4.2`); the resolved exact patch is pinned in the generated POM |
| Output location | Both the project directory and sibling ZIP must be available; existing outputs are not overwritten |

The generator queries Maven Central for a stable patch. Eligible connection failures use a reported bundled reference; other lookup failures stop generation. See the [CLI resolution contract](cli.md#spring-boot-patch-resolution).

## Migrate a generation recipe

1. Run the generator with JDK 25. If deployment uses JDK 21, add `--java 21` and switch to JDK 21 before building the generated project.
2. Replace Boot 3.x selectors with `--boot 4.1` or `--boot 4.2`. Inspect the resolved patch and source in the CLI output and check the generated POM into the consuming repository.
3. Use a fresh target directory to compare an old scaffold with 2.0 output. Review the Boot parent, MVC dependencies, test phases and generated rule names as part of that comparison.
4. Run the generated wrapper's `clean verify` and review Surefire and, with samples enabled, Failsafe reports. Apply framework migrations to an existing application in its own repository.

Historical version entries are in the [changelog](../CHANGELOG.md). Publication readiness and the release matrix are recorded in the [maintainer checklist](maintainers/release-discipline.md).

## Generated build and delivery

- Standard and Hexagonal retain generated ArchUnit guardrails. Strict REST signature rules also activate when the basic sample implicitly adds MVC.
- The basic sample includes real HTTP integration tests in Maven verification; the generated project resolves its Mockito agent through Maven dependencies.
- Generated POM text is XML-escaped. Publication protects existing outputs and attempts ownership-aware cleanup on failure; it is not crash-atomic.

Before upgrading, review the [compatibility matrix](compatibility.md), [CLI reference](cli.md) and [delivery limitations](architecture/executable-architecture-contract.md#delivery-and-reproducibility).
