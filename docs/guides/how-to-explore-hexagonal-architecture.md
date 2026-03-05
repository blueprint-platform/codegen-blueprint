# Codegen Blueprint — Hexagonal Architecture Deep Dive

**Executable Architecture in Action — A Production-Grade Reference**

This deep-dive explains **how Hexagonal Architecture (Ports & Adapters)** is
**executed and continuously verified** in Codegen Blueprint — not as guidelines,
but as **generated, testable behavior**.

Architectural decisions are **compiled into the generator and materialized in the output**:

* Domain stays 🔒 framework-free
* Technology swaps 🔁 without core changes
* Architectural intent 🚧 made executable via generated artifacts
* Generated services 🧱 inherit structure by construction

> **Architecture is not a guideline — it executes.**

---

## 📑 Table of Contents

* [Why Hexagonal Here?](#-why-hexagonal-here)
* [Layered Execution Flow](#-layered-execution-flow)
* [Ports & Adapters — Where the Power Lives](#-ports--adapters--where-the-power-lives)
* [Domain → Outbound Ports](#-domain--outbound-ports-pure-infrastructure-abstractions)
* [Application → Outbound Ports](#-application--outbound-ports-delivery--orchestration)
* [Application → Artifact Generation Ports](#-application--artifact-generation-ports)
* [Artifact Execution Engine](#-artifact-execution-engine)
* [Profiles — The Architecture Contract](#-profiles--the-architecture-contract)
* [Source Layout Guardrails](#-source-layout-guardrails)
* [Resource Model](#-resource-model--better-than-just-files)
* [Verified Architecture — Testing Strategy](#-verified-architecture--testing-strategy)
* [What You Learn from This Repo](#-what-you-learn-from-this-repo)
* [Application Inbound Port (Use Case Boundary)](#-application-inbound-port-use-case-boundary)
* [Try It — CLI Delivery Adapter](#-try-it--cli-delivery-adapter)
* [Architecture Execution Path](#-architecture-execution-path-mental-model)
* [Final Thoughts](#-final-thoughts)
* [Appendix — Port Placement Policy (Domain vs Application)](#appendix--port-placement-policy-domain-vs-application)

---

## 🧭 Why Hexagonal Here?

Most project templates generate **folders**.
Codegen Blueprint generates **architectural intent**.

Hexagonal Architecture is not a stylistic choice in this project.
It is a **prerequisite** for Blueprint’s core promise:

> **Generate once.  
> Evolve across frameworks, runtimes, and languages — without rewriting the core.**

### Why this matters

Blueprint Platform is designed to be:

- profile-driven
- framework-agnostic at its core
- extensible across stacks over time

This is only possible if:

- the **domain model remains pure and framework-free**
- application use cases are expressed via **ports**
- all technology decisions live behind **replaceable adapters**

Without strict hexagonal boundaries:

- every new framework would leak into core logic
- profiles would become forks
- extensibility would collapse into copy-paste generators

---

### What Hexagonal enables here

| Hexagonal Principle           | Platform Capability Enabled                                  |
| ----------------------------- | ------------------------------------------------------------ |
| Inward dependency direction   | Domain & application remain stable across profiles           |
| Ports define use-case contracts | New delivery mechanisms (CLI, REST, future APIs) plug in safely |
| Adapter isolation             | Frameworks and languages evolve without core rewrites        |
| Profile-driven adapters       | New stacks are added by extension, not modification          |

> **Spring Boot is the first adapter — not the foundation.**  
> Hexagonal Architecture ensures it is never the last.

---

## 🧱 Layered Execution Flow

Strict inward dependency:

```
adapter (delivery + tech)
        ↓
application (use cases, orchestration)
        ↓
domain (business rules only)
```

Runtime wiring is delivered via `bootstrap` (Spring only at the edges).

* 📌 No Spring inside `domain`
* 📌 No template engine inside `domain` (templating lives in adapters; the application only orchestrates generation via ports)
* 📌 No file system assumptions inside business logic

---

## 🔌 Ports & Adapters — Where the Power Lives

Ports define **what is allowed**.
Adapters define **how it is done**.

No shortcuts. No hidden dependencies.

---

## 🧠 Domain → Outbound Ports (Pure Infrastructure Abstractions)

These ports represent **fundamental IO capabilities** required by the domain.
The domain **declares the need**, but never performs IO itself.

| Port                     | Responsibility                              |
| ------------------------ | ------------------------------------------- |
| `ProjectRootPort`        | Prepare and validate project root directory |
| `ProjectWriterPort`      | Persist generated files and directories     |

**Key characteristics:**

* ✔ No ZIP / archive knowledge
* ✔ No delivery concerns
* ✔ No CLI / REST assumptions
* ✔ File‑system is an **implementation detail**

```
domain.port.out.filesystem
├─ ProjectRootPort
├─ ProjectWriterPort
```

➡ Domain never touches IO implementations <br>
➡ Domain never packages output

---

## 🎯 Application → Outbound Ports (Delivery & Orchestration)

The application layer owns **use-case execution and delivery concerns**.

| Port                  | Responsibility                                                  |
| --------------------- | --------------------------------------------------------------- |
| `ProjectArchiverPort` | Package generated project (ZIP today, OCI tomorrow)             |
| `ProjectOutputPort`   | Discover generated project output for reporting & delivery UX   |

```
application.port.out.archive
└─ ProjectArchiverPort

application.port.out.output
└─ ProjectOutputPort
```

➡ Packaging and output discovery are **not domain concerns**  
➡ They are **delivery / orchestration mechanisms**  
➡ Therefore they belong to the **application layer**

---

## 🧩 Application → Artifact Generation Ports

Each generated artifact is **explicit**, **intentional**, and **independently replaceable**.

Artifact generation is modeled as a **first‑class application concern** — not a side effect of templates.

| Port                           | Generated Output / Responsibility  |
| ------------------------------ | ---------------------------------- |
| `BuildConfigurationPort`       | `pom.xml`                          |
| `BuildToolFilesPort`           | Maven wrapper + tooling            |
| `SourceLayoutPort`             | Directory & package conventions    |
| `MainSourceEntrypointPort`     | Application bootstrap class        |
| `TestSourceEntrypointPort`     | Test bootstrap                     |
| `ApplicationConfigurationPort` | `application.yml`                  |
| `IgnoreRulesPort`              | `.gitignore`                       |
| `ProjectDocumentationPort`     | `README.md`                        |
| `SampleCodePort`               | Optional sample REST / domain code |
| `ArchitectureGovernancePort`   | Architecture guardrails artifacts |

All artifact ports implement:

```
application.port.out.artifact.ArtifactPort
```

### Architecture Governance as an Artifact

`ArchitectureGovernancePort` models **architecture guardrails itself** as a generated artifact.

Depending on profile and guardrails level, this may generate:

* ArchUnit‑based architecture tests
* Layered or Hexagonal boundary rules
* Dependency direction constraints

Enforcement artifacts are:

* ✔ Generated (not hard‑wired)
* ✔ Opt‑in (`--guardrails basic | strict`)
* ✔ Profile‑scoped
* ✔ Evolvable without engine refactors

> Architecture guardrails is **delivered as code**, like any other artifact.

---

## ⚙️ Artifact Execution Engine

Artifact generation is **ordered**, **deterministic**, and **profile‑driven**.

| Component                  | Responsibility                             |
| -------------------------- | ------------------------------------------ |
| `ProjectArtifactsSelector` | Selects profile‑specific artifact pipeline |
| `ProjectArtifactsPort`     | Executes artifacts in defined order        |

> Nothing is generated accidentally — every file is **architecturally intentional**.

---

## 🧬 Profiles — The Architecture Contract

Profiles externalize **what is generated** and **in which order**.

Example pipeline:

```
build-config
→ build-tool-files
→ ignore-rules
→ source-layout
→ app-config
→ main-source-entrypoint
→ test-source-entrypoint
→ architecture-governance (optional)
→ sample-code (optional)
→ project-documentation
```

> The pipeline is executed **top-to-bottom** in the exact order defined by the profile.
> `build-config` is always the first artifact executed and establishes the build foundation
> for everything that follows.

Profiles are:

* ✔ Encode architecture standards explicitly
* ✔ Reusable across teams
* ✔ Extensible without core changes

---

## 📐 Source Layout Guardrails

### Standard

```
src/main/java/<basepkg>/
src/main/resources/
src/test/java/<basepkg>/
```

### Hexagonal (opt‑in)

```
adapter/
  ├─ in/
  └─ out/
application/
domain/
bootstrap/
```

---

## 📂 Resource Model — Better than “Just Files”

| Type      | Domain Model              | Why                 |
| --------- | ------------------------- | ------------------- |
| Directory | `GeneratedDirectory`      | Validated structure |
| Text      | `GeneratedTextResource`   | Safe content        |
| Binary    | `GeneratedBinaryResource` | Tooling & wrappers  |

> Generated resources are modeled, validated, and ordered — not written ad-hoc.

---

## 🧪 Verified Architecture — Testing Strategy

| Test Type   | Ensures                             |
| ----------- |-------------------------------------|
| Unit        | Domain & rule correctness           |
| Integration | Correct wiring                      |
| E2E CLI     | Generated project validity          |
| ArchUnit    | Executable architectural boundaries |

Together, these tests ensure that architectural intent is continuously validated
from generation time through build execution.

> Tests protect **architecture**, not just syntax.
> Architecture tests protect *dependency direction*, not implementation details.

---

## 🎯 What You Learn from This Repo

| Skill                    | How This Repo Makes It Explicit                              |
| ------------------------ | ------------------------------------------------------------- |
| Hexagonal mastery        | Clear dependency direction expressed through ports            |
| Maintainable scaffolding | Evolution paths encoded and made visible from day zero        |
| Architecture automation  | Architectural intent delivered as executable artifacts        |
| Multi-stack enablement   | New stacks added without changing the core engine             |
| Testing for architecture | Boundaries verified through fast, build-time feedback         |

This repository is a **production reference architecture**.

> It prioritizes architectural clarity, continuity, and evolution  
> over feature breadth or short-term convenience.

It is designed to be:
- explored by experienced developers and architects
- extended as a long-lived platform foundation
- challenged through real architectural change scenarios

It is **not positioned as** a classroom demo, step-by-step tutorial,
or framework marketing showcase.

Instead, it serves as a **production-oriented reference** for teams and individuals
who want to **observe, evaluate, and evolve executable architecture**
in real-world systems.

---

## 🎯 Application Inbound Port (Use Case Boundary)

In Codegen Blueprint, the **primary hexagonal boundary** at the application edge is an **inbound port** (a use-case contract):

- `application.port.in.project.CreateProjectPort`

This is **not a domain port**.
It represents a **use case entrypoint** owned by the application layer.

Delivery mechanisms (CLI today, REST tomorrow) are **inbound adapters** that translate input into a `CreateProjectCommand` and invoke this port.

```java
public interface CreateProjectPort {
    CreateProjectResult handle(CreateProjectCommand createProjectCommand);
}
```
**Key idea:**  
Adapters depend on the port.  
The port does **not** depend on adapters.

---

## 🎮 Try It — CLI Delivery Adapter

This section demonstrates the **actual, supported CLI contract** for Codegen Blueprint **1.0.0 GA** when generating a **Hexagonal Architecture** project.

The command below reflects the real engine behavior, generated structure, and guardrails capabilities — no placeholders, no aspirational flags.

---

### Generate a Hexagonal Spring Boot Project

```bash
java -jar codegen-blueprint-<version>.jar \
  --cli springboot \
  --group-id io.github.blueprintplatform \
  --artifact-id greeting \
  --name "Greeting" \
  --description "Greeting sample built with hexagonal architecture" \
  --package-name io.github.blueprintplatform.greeting \
  --layout hexagonal \
  --guardrails strict \
  --sample-code basic \
  --dependency web \
  --target-dir /path/to/output
```
> Note: Additional `--dependency` flags may be provided.  
> This example keeps the focus on **architecture and use-case boundaries**, not infrastructure breadth.

---

### What This Command Does

This single command:

* Selects the **Spring Boot · Maven · Java 21** profile
* Generates a **hexagonal (ports & adapters)** source layout
* Produces a **framework-free domain core** by construction
* Enables **strict architecture guardrails** via generated ArchUnit tests
* Adds a **minimal teaching sample** (domain + application + adapter)
* Outputs a project that **builds and verifies immediately**

No runtime configuration is required.
No manual wiring is expected.

---

### Resulting Guarantees

The generated project:

* Builds and verifies successfully with `mvn verify` on first run
* Makes architectural boundaries **explicit and testable** at build time
* Surfaces unintended dependency direction changes early
* Keeps Spring consistently at the edges (`bootstrap` and `adapter` layers)

> Architecture is not implied or documented —  
> it is **made visible through executable structure and tests**.

---

This CLI adapter is the **primary delivery mechanism** of Codegen Blueprint 1.0.0 GA.  
Everything else in the system exists to make this command **predictable, transparent, and evolution-ready**.

---

## 🔍 Architecture Execution Path (Mental Model)

```
CLI input
 ↓
ProjectBlueprint
 ↓
ProjectArtifactsSelector
 ↓
ProjectArtifactsPort
 ↓
ProjectWriterPort
```

> No artifact knows about another artifact — only the pipeline knows the order.

---

## ⭐ Final Thoughts

**Executable Architecture** means:

* Architecture cannot drift accidentally
* Domain is always protected
* Technology evolves independently
* Standards are repeatable at scale

> Architecture is no longer documentation — it is **behavior**.

---

## Appendix — Port Placement Policy (Domain vs Application)

This appendix exists to clarify an intentional design decision
frequently misunderstood when reviewing hexagonal architectures.

In Codegen Blueprint, **ports are not placed arbitrarily**.
Their location reflects **ownership of responsibility**, not technical convenience.

Hexagonal Architecture allows outbound ports at **multiple layers**, as long as **dependency direction and intent remain explicit**.

---

## Domain → Outbound Ports (`domain.port.out.*`)

Domain outbound ports represent **fundamental infrastructure capabilities** required by the domain model to remain pure and executable.

**Characteristics:**

* Express **what the domain needs**, not *how it is delivered*
* No orchestration or delivery semantics
* No packaging, archiving, or presentation concerns
* No framework or runtime assumptions

**Examples:**

* `ProjectRootPort` — prepare and validate a project root
* `ProjectWriterPort` — persist generated resources

These ports model **capabilities**, not workflows.

The domain declares:

> “I need to write resources”

—not—

> “I need to zip, publish, or expose them.”

---

## Application → Outbound Ports (`application.port.out.*`)

Application outbound ports represent **use-case delivery and orchestration responsibilities**.

**Characteristics:**

* Coordinate execution and output
* Own delivery semantics (archive, listing, reporting)
* Bind multiple domain capabilities into a use-case flow
* Framework-agnostic, but delivery-aware

**Examples:**

* `ProjectArchiverPort` — package the generated project
* `ProjectOutputPort` — discover and report generated output
* `ProjectArtifactsSelector` — select and execute artifact pipelines

These ports model **how a use case is delivered**, not domain rules.

---

## Why This Split Exists

This distinction is intentional and critical for a **generation engine**:

* Domain ports remain stable across delivery mechanisms
* Application ports evolve alongside delivery strategies
* New delivery modes (CLI today, REST tomorrow) do **not** ripple into domain contracts
* Architectural rules remain **explicit, explainable, and verifiable** at build time

If all outbound ports lived in the domain, the domain would implicitly take ownership
of delivery orchestration — which it must not.

If all outbound ports lived in the application, the domain would lose the ability
to clearly express the capabilities it requires to stay pure and executable.

---

## Architectural Guarantee

* Domain depends on **domain ports only**
* Application depends on **domain + application ports**
* Adapters implement ports — never own them
* Dependency direction remains strictly inward

This is **not hexagonal drift**.
It is **explicit responsibility separation** aligned with executable architecture.