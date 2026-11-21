# Changelog

## 2.0.0 · Unreleased

- Generate Spring Boot 4.1/4.2 projects with Java 21/25. Defaults are Boot 4.2 and Java 25.
- Resolve the latest stable patch at generation time with an explicit connection-failure fallback.
- Align generated MVC dependencies and strict REST rules, run sample integration tests during verification, and protect existing output paths.
- Run the generator on Spring Boot 4.2.0 with a Java 25 compile and runtime baseline.
- Validate artifact order and fixed template configuration at startup. Reject invalid dependency scopes before platform resolution.

See the [release notes](docs/release-notes.md).

---

## [1.0.3] - 2026-05-02

### Patch release

* Spring Boot updated to **3.5.14**
* ArchUnit updated to **1.4.2** (from 1.4.1)
* Maven Wrapper updated to **3.9.15** (from 3.9.12)
* Documentation terminology alignment:

  * Replaced misleading **"opt-in"** wording
  * Clarified **default-enabled (opt-out)** behavior for guardrails
  * Clarified **defaulted layout selection** (`standard`)
  * Aligned documentation with the actual **CLI contract behavior**

No contract changes.
No generated project structure changes.
No CLI behavior changes.

Fully compatible with the **1.0.x GA contract**.

---

## [1.0.2] - 2026-03-30

### Patch release

* Spring Boot updated to **3.5.14** (deterministic patch alignment)
* README improvements for clarity and positioning

No contract changes.
No generated project structure changes.
No CLI behavior changes.

Fully compatible with the **1.0.x GA contract**.

---

## [1.0.1] - 2026-03-05

### Patch release

* Dependency updates (Spring Boot patch-level alignment)
* Documentation clarifications
* Minor internal fixes and stability improvements

No contract changes.
No generated project structure changes.
No CLI behavior changes.

Fully compatible with the **1.0.0 GA contract**.

---

## [1.0.0] - 2026-01-05

Codegen Blueprint **1.0.0 GA** marks the point where this repository becomes a **product contract** — not an experiment.

### Highlights

* **Executable proof included** — reproducible **GREEN → RED → GREEN** validation using **build-time guardrails only**.
* **Architecture guardrails (default-enabled)** — generated **ArchUnit** tests that fail **deterministically** during `mvn verify`; configurable via `--guardrails`.
* **Two architecture models** — `standard` is the default layout; `hexagonal` can be selected explicitly via `--layout`.
* **Buildable-by-default output** — generated projects pass **`mvn verify`** immediately after generation.
* **Explicit GA contract docs** — guarantees, release discipline, and non-goals are written as authoritative policy.

### GA profile and compatibility

* Active GA profile: `springboot-maven-java`
* Compatibility targets:

  * **Java 21**
  * **Spring Boot 3.4–3.5** (default: **3.5**)
  * **Maven 3.9+**

### Output contract (generated project)

Generated projects are **single-module** and include:

* `pom.xml` (with Maven Wrapper)

  * `.mvn/wrapper/maven-wrapper.properties`
  * `mvnw`
  * `mvnw.cmd`
* `src/main/java/<basePackage>/...`
* `src/test/java/<basePackage>/...`
* `src/main/resources/application.yml`
* `.gitignore`
* `README.md` (part of the generated contract surface)

### References

* Binding contract: [Executable Architecture Contract](https://github.com/blueprint-platform/codegen-blueprint/blob/68a959685612bc5e6595c8b8195d5d85ec6f9fff/docs/architecture/executable-architecture-contract.md)
* Guardrails semantics: [Architecture Guardrails Rulebook](https://github.com/blueprint-platform/codegen-blueprint/blob/68a959685612bc5e6595c8b8195d5d85ec6f9fff/docs/architecture/architecture-guardrails-rulebook.md)
