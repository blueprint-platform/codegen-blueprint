# 🚀 Codegen-Blueprint — Hexagonal Architecture Deep Dive

Welcome! This guide shows how **Hexagonal Architecture (Ports & Adapters)** is applied to a **real, production-grade project generation engine** — with strict boundaries and full test coverage.

This repository demonstrates **Executable Architecture** in action:

* Architecture rules **enforced by the engine**, not left to individuals
* Domain remains **pure and framework-agnostic**
* Technology choices are **plug-replaceable** — without core changes

> Build a scalable ecosystem of services —  
> **without losing architectural consistency over time**.

*Hexagonal Architecture — not just documented, but executed.*

---

## 📚 Table of Contents

* [🧱 Architectural Overview](#-architectural-overview)
* [🔌 Ports & Adapters](#-ports--adapters)
    * [💼 Domain → Outbound Ports](#-domain--outbound-ports)
    * [🧩 Application → Artifact Generation Ports](#-application--artifact-generation-ports)
    * [🛠️ Technology Adapters](#-technology-adapters)
* [📦 Profiles: Externalized Architecture Rules](#-profiles-externalized-architecture-rules)
* [🧱 Source Layout Generation](#-source-layout-generation)
* [📄 Resource Model — Stronger than “Files”](#-resource-model--stronger-than-files)
* [🧪 Testing Strategy](#-testing-strategy)
* [🎯 What You Can Learn Here](#-what-you-can-learn-here)
* [🎮 Try It — CLI Adapter](#-try-it--cli-adapter)
* [🔍 Start Here](#-start-here)
* [⭐ Final Thoughts](#-final-thoughts)

---

## 🧱 Architectural Overview

Codegen Blueprint applies **strict inward dependency flow** — ensuring the **domain stays pure** and fully independent of frameworks:

```
bootstrap   // Spring & runtime wiring only
↓
adapter     // technology-specific implementations (CLI, File, Templating…)
↓
application // orchestration, profiles, generation rules
↓
domain      // core business rules — no external dependencies
```

### Key Principles

* **Domain-centric** — business logic remains framework-free
* **Replaceable adapters** — switch technology with no core changes
* **Independent testing** — every layer testable on its own
* **Evolution-ready** — new profiles or stacks plug in without refactor

> Architecture is not a *guideline* here —  
> **it is enforced by design**

---

## 🔌 Ports & Adapters

The engine is **driven by ports (interfaces)** — fully decoupled from frameworks.

---

### 💼 Domain → Outbound Ports

These ports allow the **application layer** to perform external actions  
**without** depending on external technology:

| Port                  | Responsibility                                        |
| --------------------- | ----------------------------------------------------- |
| `ProjectRootPort`     | Resolve and prepare the output project directory      |
| `ProjectWriterPort`   | Persist generated resources (text / binary / folders) |
| `ProjectArchiverPort` | Bundle project for delivery (e.g., ZIP packaging)     |

> Same domain → multiple tech stacks → zero changes to business rules

---

### 🧩 Application → Artifact Generation Ports

Each artifact in the produced project has a **dedicated generation port**:

| Port                           | Output artifact                                        |
| ------------------------------ | ------------------------------------------------------ |
| `SourceLayoutPort`             | Java package structure & source folders                |
| `MainSourceEntrypointPort`     | Main application class                                 |
| `TestSourceEntrypointPort`     | Test bootstrap                                         |
| `ApplicationConfigurationPort` | Runtime configuration (`application.yml`)              |
| `BuildConfigurationPort`       | Build descriptor (`pom.xml`)                           |
| `BuildToolFilesPort`           | Wrapper + tool metadata (`mvnw`, `.mvn/`)              |
| `IgnoreRulesPort`              | `.gitignore` + VCS hygiene                             |
| `ProjectDocumentationPort`     | Generated project README                               |
| `SampleCodePort`               | Optional greeting sample (domain + ports + REST demo)  |

Supporting the pipeline:

| Component                  | Role                                                |
| -------------------------- | --------------------------------------------------- |
| `ProjectArtifactsPort`     | Executes artifacts in correct architectural order   |
| `ProjectArtifactsSelector` | Chooses implementation based on selected TechStack  |

> Every artifact is intentional → nothing accidental is generated

---

### 🛠️ Technology Adapters

Adapters **implement ports using real world tooling**:

* File system access
* FreeMarker-based resource templating
* Maven build metadata

Designed for evolution:

* Gradle
* Kotlin
* Quarkus
* REST delivery

⬆ All can be added **without touching domain or application code**

---

> **Ports define the architecture**  
> **Adapters only enable execution**

---

## 📦 Profiles: Externalized Architecture Rules

Profiles define what artifacts are generated, in what order, and under which architecture rules:

* Template namespace (profile defines rendering folders)
* Enabled artifacts per stack
* Strict generation ordering — architecture enforcement

📍 Example — `springboot-maven-java` 1.0.0 pipeline

```
build-config → build-tool-files → ignore-rules
→ source-layout → app-config
→ main-source-entrypoint → test-source-entrypoint
→ sample-code (optional)
→ project-documentation
```

> Profiles ensure **hexagonal evolution** does not require code changes — only configuration.
</br>

---

## 🧱 Source Layout Generation

`SOURCE_LAYOUT` adapter now generates:

### Standard Layout

```
src/main/java/<basepkg>/
src/main/resources/
src/test/java/<basepkg>/
src/test/resources/
```

### Hexagonal layout (opt-in an evolution path)

```
src/main/java/<basepkg>/
├─ domain/
├─ application/
├─ adapter/
│   ├─ in/
│   └─ out/
└─ bootstrap/
```

If `--sample-code basic` is enabled:

```
adapter/in/rest/
adapter/out/ (future)
domain/greeting/
application/greeting/
```

> Directories are **intentional artifacts** → not side effects.

---

## 📄 Resource Model — Stronger than “Files”

Generated assets are modeled as first-class domain concepts:

| Type      | Model                      | Purpose                        |
| --------- | -------------------------- | ------------------------------ |
| Directory | `GeneratedDirectory`       | Ensure structural correctness  |
| Text      | `GeneratedTextResource`    | Java, YAML, README, etc.       |
| Binary    | `GeneratedBinaryResource`  | Maven wrapper, future assets   |

Capability highlights:

* Template-driven & template-less generation
* Supports future binary artifacts (zip, images)
* Perfect fit for multi-artifact pipelines


---

## 🧪 Testing Strategy

| Test Type                | Validates                                          |
| ------------------------ | ------------------------------------------------- |
| **Unit Tests**           | Domain rules + adapter logic                      |
| **Integration Tests**    | Spring wiring + ordered artifact pipeline         |
| **E2E CLI Tests**        | Full generation → ZIP structure correctness       |
| **Template Coverage**    | Sample code, structure, placeholders, UTF-8 model |

CI includes:

* 🧩 Contract tests for every port + adapter pair
* 📊 Codecov tracking — full pipeline validation
* 🔐 CodeQL security scanning
* ✔ Architectural test gates planned (`ArchUnit`)

### Summary

* Profiles externalize **architecture rules**
* Layout generation enforces **predictability**
* Resource model prevents **accidental drift**
* Tests safeguard **contract integrity**

---

## 🎯 What You Can Learn Here

| Capability You’ll Gain     | How This Repo Enables It                                  |
|---------------------------|-----------------------------------------------------------|
| Hexagonal architecture    | Strict boundaries, port-driven domain isolation           |
| Code generation engines   | Profile-driven, ordered artifact pipelines                |
| Enterprise maintainability| Add new stacks w/o modifying core engine                 |
| CI-First delivery         | Coverage, contract tests, secure pipelines               |
| Architecture automation   | Enforce structure from day zero — “Executable Architecture” |

This is a **real production reference**, not a conceptual demo.

---

## 🎮 Try It — CLI Adapter

Here’s the **springboot-maven-java** profile with **hexagonal** layout and **sample greeting** included:

```bash
java -jar codegen-blueprint-1.0.0.jar \
  --cli \
  springboot \
  --group-id io.github.blueprintplatform.samples \
  --artifact-id greeting-service \
  --name "Greeting Service" \
  --description "Hexagonal greeting sample powered by Blueprint" \
  --package-name io.github.blueprintplatform.samples.greeting \
  --layout hexagonal \
  --sample-code basic \
  --dependency web \
  --dependency data_jpa
```

This produces a ready-to-run service with a REST greeting endpoint:

```bash
GET /api/v1/sample/greetings/default

→ 200 OK: "Hello from hexagonal sample!"
```


Run instantly:
cd greeting-service
./mvnw spring-boot:run

---

## 🔍 Start Here

Follow the architecture execution path:

```
[ CLI input ] 
      ↓
ProjectBlueprint
      ↓
ProjectArtifactsSelector  // chooses profile implementation
      ↓
ProjectArtifactsPort      // executes ordered ports
      ↓
ProjectWriterPort         // writes physical output (FS/ZIP)
```

You are watching architecture → compiled and executed.

---

## ⭐ Final Thoughts

Executable Architecture means:
* 🚫 No framework leaking into domain logic
* 🧠 Architecture intent is automated, not “documented & forgotten”
* ♻️ Adaptable tech stacks w/o core rewrites
* 🧪 Full test enforcement from pipeline to template

Built for teams who believe:

“Architecture isn’t a diagram — it’s a behavior that must execute.”

Happy generating! 🚀✨