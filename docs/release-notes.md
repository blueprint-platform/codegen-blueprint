# 2.0.0 release notes

The current branch builds `2.0.0-SNAPSHOT`. The 2.0.0 binary is unpublished.

## Before upgrading from 1.x

- **Generator runtime** requires JDK 25 and uses Spring Boot 4.2.0 internally.
- **Generated targets** move from Boot 3.x to Boot 4.1 or 4.2. Boot 4.0 is outside the supported target set. Use `--boot 4.1` or `--boot 4.2`. The selector takes a minor line and the generated POM pins the resolved patch. Eligible connection failures use a reported bundled reference.
- **Generation defaults** are Boot 4.2 and Java 25. Use `--java 21` to keep a Java 21 target.
- **Output paths** must be available. Choose a fresh target if a project directory or sibling ZIP already exists.

See [compatibility](compatibility.md) for verified targets and the [CLI reference](cli.md#spring-boot-patch-resolution) for patch selection and failures.

## Generated build changes

Standard and Hexagonal retain executable guardrails. Strict REST signature checks also run when the basic sample implicitly adds MVC. Maven verification includes the sample’s real HTTP integration tests and resolves the Mockito agent from the generated project’s dependencies.

POM text is XML-escaped. Publication preserves existing outputs and attempts cleanup of owned paths on failure. The [delivery contract](architecture/executable-architecture-contract.md#delivery-and-reproducibility) describes filesystem limits.
