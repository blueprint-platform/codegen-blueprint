<h1 align="center">Codegen Blueprint</h1>

<p align="center"><strong>Architecture as an Executable Contract</strong></p>

<p align="center">
  <a href="https://github.com/blueprint-platform/codegen-blueprint/actions/workflows/build.yml?query=branch%3Amain"><img alt="Build and Test on main" src="https://github.com/blueprint-platform/codegen-blueprint/actions/workflows/build.yml/badge.svg?branch=main"></a>
  <a href="https://app.codecov.io/gh/blueprint-platform/codegen-blueprint/tree/main"><img alt="Codecov coverage on main" src="https://codecov.io/gh/blueprint-platform/codegen-blueprint/branch/main/graph/badge.svg"></a>
</p>
<p align="center">
  <a href="docs/compatibility.md"><img alt="Generator: Java 25" src="https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white"></a>
  <a href="docs/compatibility.md"><img alt="Generated targets: Spring Boot 4.1 and 4.2" src="https://img.shields.io/badge/Spring%20Boot-4.1%20%2F%204.2-6DB33F?logo=springboot&logoColor=white"></a>
  <a href="LICENSE"><img alt="License: MIT" src="https://img.shields.io/badge/License-MIT-blue"></a>
</p>

Generate a **Standard or Hexagonal Spring Boot project** whose Maven build checks the architectural boundaries you select. Each project includes its own wrapper, source tree and, unless disabled, generated ArchUnit tests. The generator also delivers a sibling ZIP.

**[Quickstart](docs/quickstart.md)** · [CLI options](docs/cli.md) · [Documentation](docs/index.md)

## Generate a project

Build the unreleased 2.0 snapshot with **JDK 25** and **Maven 3.9.16**:

```bash
mvn -B -ntp clean package
```

Then follow the [verified quickstart](docs/quickstart.md) to generate a Hexagonal sample with Strict guardrails and run `./mvnw clean verify`. The generated build exercises architecture assertions and, with the basic sample, real HTTP integration tests.

## Choose your architecture

| Layout | Structure | Guardrails |
| --- | --- | --- |
| **Standard** (default) | Controllers, services, repositories and domain code | Layer direction, cycles and schema; additional Strict boundaries |
| **Hexagonal** | Application ports and use cases, domain code, adapters and bootstrap wiring | Inward dependencies, cycles and schema; additional Strict isolation |

Choose `basic` (default) for core checks, `strict` for additional layout-specific rules, or `none` to generate the structure without ArchUnit. The [rulebook](docs/architecture/architecture-guardrails-rulebook.md) defines the exact assertions; the [architecture contract](docs/architecture/executable-architecture-contract.md) explains dependency directions and limitations.

## See an architectural violation fail the build

A generated Standard project with Strict guardrails initially passes verification. Adding a repository-typed field to its controller **still compiles**, but ArchUnit rejects the dependency. Restoring the source makes the build pass again.

![Architecture proof: passing baseline, compiling controller-to-repository violation rejected by ArchUnit, then passing restoration.](docs/assets/architecture-proof.png)

[View the Java mutation, named assertion and reproduction steps](docs/examples/boundary-proof.md).

## What's different in 2.0?

**1.x already included executable architecture guardrails.** Version 2.0 changes the generation contract: the generator runs on Java 25 with Spring Boot 4.2.0 internally; generated projects target Spring Boot **4.1 or 4.2** and Java **21 or 25**. Boot 3.x and 4.0 are no longer targets. An exact Boot patch is resolved and pinned during generation, with a documented connection-failure fallback.

This branch builds **`2.0.0-SNAPSHOT`**, not a published 2.0 release. See the [release notes](docs/release-notes.md) and [compatibility matrix](docs/compatibility.md).

---

[Contributing](CONTRIBUTING.md) · [Security](SECURITY.md) · [MIT license](LICENSE)
