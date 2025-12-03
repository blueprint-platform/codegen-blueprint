# Codegen Blueprint — Enterprise‑Grade, Hexagonal, Architecture‑First Project Generator

[![Build](https://github.com/bsayli/codegen-blueprint/actions/workflows/build.yml/badge.svg)](https://github.com/bsayli/codegen-blueprint/actions/workflows/build.yml)
[![Release](https://img.shields.io/github/v/release/bsayli/codegen-blueprint?logo=github\&label=release)](https://github.com/bsayli/codegen-blueprint/releases/latest)
[![CodeQL](https://github.com/bsayli/codegen-blueprint/actions/workflows/codeql.yml/badge.svg)](https://github.com/bsayli/codegen-blueprint/actions/workflows/codeql.yml)
[![codecov](https://codecov.io/gh/bsayli/codegen-blueprint/branch/refactor/hexagonal-architecture/graph/badge.svg)](https://codecov.io/gh/bsayli/codegen-blueprint/tree/refactor/hexagonal-architecture)
[![Java](https://img.shields.io/badge/Java-21-red?logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9-blue?logo=apachemaven)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📑 Table of Contents

* 🧭 [Project Status & Release Plan](#-project-status--release-plan)
* 💡 [Why This Project Matters](#-why-this-project-matters)
* 🚀 [Vision — Architecture as a Product](#-vision--architecture-as-a-product)
* 🧱 [Architectural Model](#-architectural-model-pure-hexagonal)
* 🔌 [Inbound Adapters](#-inbound-adapters-delivery-channels)
* ⚙️ [Outbound Adapters & Artifact Rendering](#-outbound-adapters--artifact-rendering)
* 🧪 [Testing & CI](#-testing--ci)
* 🔄 [Example CLI Usage](#-example-cli-usage)
* 🛣 [Roadmap](#-roadmap)
* 🤝 [Contributing](#-contributing)
* 🛡 [License](#-license)

---

## 🧭 Project Status & Release Plan

This repository is in **active development** toward **1.0.0 GA**.

✔ Hexagonal domain, pipeline engine, templating system, CI/CD, and test suite are complete.
✔ **CLI inbound adapter** is implemented — production‑ready project generation via terminal.
🔄 **REST inbound adapter** will follow shortly.

This is not a typical "initializr clone" — this is a **blueprint engine** with real architectural guarantees.

---

## 💡 Why This Project Matters

Modern microservices demand **more than folder scaffolding**.

Teams need new services to start **production‑ready**:

✓ Clean architecture enforced from day 0
✓ Resilience, security, observability baked‑in
✓ Unified engineering standards across the organization
✓ Zero dependency on who is starting the service

But today — everywhere:

❌ Copy/paste project templates
❌ Inconsistent configurations
❌ Best practices as optional “docs nobody reads”
❌ Every squad reinvents the wheel

> **This project eliminates variability at Day 0** — the platform becomes the standard.

---

## 🚀 Vision — Architecture as a Product

Codegen Blueprint enables:

| Capability                           | Value Delivered                                         |
| ------------------------------------ | ------------------------------------------------------- |
| **Architecture Profiles**            | Choose hexagonal, layered, CQRS etc. at generation time |
| **Tech Stack Variation**             | Spring → then Quarkus, Micronaut, Vert.x                |
| **Production Essentials by Default** | CB/Retry, tracing, metrics, versioning                  |
| **Security First**                   | OAuth2 / Keycloak integration ready to toggle           |
| **Consistent DevEx**                 | Unified structure across all teams                      |

Your internal engineering rules become:

→ **Automated** ❌ no docs needed
→ **Repeatable** ❌ no tribal knowledge
→ **Enforced** ✔ from the first commit

---

## 🧱 Architectural Model (Pure Hexagonal)

*Domain is king — NO Spring dependencies inside.*

Layers:

```
domain
└─ model (aggregate, VOs, policies)
application
└─ use cases orchestrating ports
adapter
├─ outbound (renderers, build files, deps)
└─ inbound (CLI, REST)
bootstrap
└─ wiring (profiles → adapters → engine)
```

Ports define intent — adapters define technology.

Switching Spring Boot → Quarkus?

➡ Add adapter package + new templates
➡ Core engine **does not change**

---

## 🔌 Inbound Adapters (Delivery Channels)

| Adapter      | Status                                     |
| ------------ | ------------------------------------------ |
| **CLI**      | ✔ Complete (primary driver)                |
| **REST API** | 🔄 In progress (service‑driven automation) |

---

## ⚙ Outbound Adapters & Artifact Rendering

Current Profile:

```
springboot-maven-java
```

Implements ArtifactKeys:

* Maven POM
* Maven Wrapper
* `.gitignore`
* Application YAML
* Main Source Entrypoint
* Test Entrypoint
* Documentation

Upcoming adapters:

* Gradle
* Kotlin
* Multi‑module
* CI/CD
* Dockerfile

---

## 🧪 Testing & CI

```bash
mvn verify
```

✔ Full integration tests
✔ JaCoCo coverage
✔ CodeQL security scanning
✔ Codecov reporting

---

## 🔄 Example CLI Usage

```bash
java -jar codegen-blueprint.jar \
  springboot \
  --group-id com.example \
  --artifact-id demo \
  --name "Demo Service" \
  --package-name com.example.demo \
  --dependency WEB
```

Output:

```text
demo/
 ├── pom.xml
 ├── src/main/java/.../DemoApplication.java
 ├── src/main/resources/application.yml
 ├── src/test/java/.../DemoApplicationTests.java
 └── .gitignore
```

---

## 🛣 Roadmap

* **Architecture style selection (hexagonal, layered, CQRS, etc.)**
* Spring Security & Keycloak integration option
* Circuit breaker + retry + tracing + metrics options
* Multi‑module enterprise layouts
* Developer portal integration (Backstage etc.)

This is how platform engineering becomes **automated**.

---

## 🤝 Contributing

Ideas & PRs welcome! 🙌

🔗 [https://github.com/bsayli/codegen-blueprint](https://github.com/bsayli/codegen-blueprint)

---

## 🛡 License

MIT — Free for all.

---

**Author:** Barış Saylı
GitHub: [https://github.com/bsayli](https://github.com/bsayli)
