# Codegen Blueprint — Architecture-First Project Generator

[![Build on main](https://github.com/blueprint-platform/codegen-blueprint/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/blueprint-platform/codegen-blueprint/actions/workflows/build.yml?query=branch%3Amain) [![Codecov on main](https://codecov.io/gh/blueprint-platform/codegen-blueprint/branch/main/graph/badge.svg)](https://app.codecov.io/gh/blueprint-platform/codegen-blueprint/tree/main)

<p align="center">
  <img src="docs/images/cover/cover.png" alt="Codegen Blueprint cover with a Hexagonal project generation command in a terminal panel." width="700" />
</p>

> Version 2.0 generates a Maven project and a sibling ZIP. By default, the project's build checks the selected architecture with generated ArchUnit tests.

---

## 🧪 Executable architecture proof

A generated **Standard** project with **Strict** guardrails passed Maven verification on JDK 25 with Spring Boot 4.2.0. Adding a repository import and this field to `GreetingController` compiled successfully.

```java
private LoggingGreetingAuditRepository __archViolation = null;
```

Surefire reported `controllers_must_not_depend_on_repositories`. Removing the import and field restored the passing build. [Reproduce the GREEN → RED → GREEN run](docs/examples/boundary-proof.md).

## ⚡ Quick start

From the repository root, package the 2.0 snapshot with **JDK 25** and **Maven 3.9.16**.

```bash
mvn -B -ntp clean package
```

Follow the [quickstart](docs/quickstart.md) to generate a runnable Hexagonal sample and verify it with the generated Maven wrapper. The walkthrough checks both the Strict architecture rules and the sample's HTTP integration tests.

## 🧱 Choose the architecture

- **Standard** (default) gives each feature controller, service, repository and domain packages.
- **Hexagonal** puts use cases behind application ports and wires concrete adapters in bootstrap.

The default `--guardrails basic` checks core dependencies, package structure and cycles. Use `strict` for additional layout rules, including REST signature checks when Spring MVC is present, or `none` for the layout alone. [Guardrails rulebook](docs/architecture/architecture-guardrails-rulebook.md)

## ⚙️ Choose the generated target

The generator runs on **Java 25** with Spring Boot **4.2.0** internally. For the output project, select Spring Boot **4.1 or 4.2** and Java **21 or 25** with `--boot` and `--java`. The defaults are Boot **4.2** and Java **25**.

The CLI resolves and pins an exact Boot patch in the generated POM. Eligible connection failures emit a warning and select a bundled reference. [CLI options and patch resolution](docs/cli.md) · [Verified targets](docs/compatibility.md)

---

[Documentation](docs/index.md) · [Contributing](CONTRIBUTING.md) · [MIT license](LICENSE)
