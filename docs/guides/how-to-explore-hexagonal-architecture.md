# 🚀 Codegen Blueprint — Hexagonal Architecture Deep Dive

**Executable Architecture in Action — A Production‑Grade Reference**

This deep‑dive explains **exactly how Hexagonal Architecture (Ports & Adapters)** is enforced in Codegen Blueprint — not as documentation, but **as behavior**.

Architecture decisions are **compiled into the generator itself**:

* Domain stays 🔒 framework‑free
* Technology swaps 🔁 without core changes
* Best practices 🚧 enforced automatically
* Generated services 🧱 inherit structure by design

> **Architecture is not a guideline — it executes.**

## 📑 Table of Contents

- [Why Hexagonal Here?](#-why-hexagonal-here)
- [Layered Execution Flow](#-layered-execution-flow)
- [Ports & Adapters — Where the Power Lives](#-ports--adapters--where-the-power-lives)
- [Domain → Outbound Ports](#-domain--outbound-ports-pure-infrastructure-abstractions)
- [Application → Outbound Ports](#-application--outbound-ports-delivery--orchestration)
- [Application → Artifact Generation Ports](#-application--artifact-generation-ports)
- [Artifact Execution Engine](#-artifact-execution-engine)
- [Profiles — The Architecture Contract](#-profiles--the-architecture-contract)
- [Source Layout Enforcement](#-source-layout-enforcement)
- [Resource Model](#-resource-model--better-than-just-files)
- [Verified Architecture — Testing Strategy](#-verified-architecture--testing-strategy)
- [What You Learn from This Repo](#-what-you-learn-from-this-repo)
- [Try It — CLI Delivery Adapter](#-try-it--cli-delivery-adapter)
- [Architecture Execution Path](#-architecture-execution-path-mental-model)
- [Final Thoughts](#-final-thoughts)

---

## 🧭 Why Hexagonal Here?

Most project templates generate: **folders**.
Blueprint generates: **architectural intent**.

Hexagonal was chosen because it delivers:

| Principle                   | Value Delivered                  |
| --------------------------- | -------------------------------- |
| Strict dependency direction | Pure, independent domain model   |
| Ports define contract       | Tech swap without refactor       |
| Adapter isolation           | Framework choice does not leak   |
| Test‑first boundaries       | Faster evolution with confidence |

> The output already **protects the future architecture** of your service.

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
* 📌 No FreeMarker inside `domain` or `application`
* 📌 No file system assumptions inside business logic

<p align="center"><em>See also: Architecture Overview diagram</em></p>

---

## 🔌 Ports & Adapters — Where the Power Lives

Ports define **what is allowed**.
Adapters define **how it is done**.

No shortcuts. No hidden dependencies.

---

## 🧠 Domain → Outbound Ports (Pure Infrastructure Abstractions)

These ports represent **fundamental IO capabilities** required by the domain.
The domain **declares the need**, but never performs IO itself.

> Domain declares **infrastructure capabilities it depends on** —
> not generation steps, not delivery strategy.

| Port                     | Responsibility                              |
| ------------------------ | ------------------------------------------- |
| `ProjectRootPort`        | Prepare and validate project root directory |
| `ProjectWriterPort`      | Persist generated files and directories     |
| `ProjectFileListingPort` | List generated files after project creation |

### Key Characteristics

* ✔ No ZIP / archive knowledge
* ✔ No delivery concerns
* ✔ No CLI / REST assumptions
* ✔ File-system is an **implementation detail**

```text
domain.port.out.filesystem
├─ ProjectRootPort
├─ ProjectWriterPort
└─ ProjectFileListingPort
```

➡ **Domain never touches IO implementations**
➡ **Domain never packages output**

---

## 🎯 Application → Outbound Ports (Delivery & Orchestration)

Application layer owns **use-case execution** and **delivery concerns**.

These ports exist because:

* The domain does not care *how* output is delivered
* The application **does**

### Delivery / Packaging

| Port                  | Responsibility                                      |
| --------------------- | --------------------------------------------------- |
| `ProjectArchiverPort` | Package generated project (ZIP today, OCI tomorrow) |

```text
application.port.out.archive
└─ ProjectArchiverPort
```

➡ ZIP creation is **not a domain concern**
➡ It is a **delivery mechanism**, therefore application-level

---

## 🧩 Application → Artifact Generation Ports

Each generated artifact is **explicit**, **intentional**, and **independently replaceable**.

| Port                           | Generated Output                   |
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

All artifact ports implement:

```text
application.port.out.artifact.ArtifactPort
```

---

## ⚙️ Artifact Execution Engine

Artifact generation is **ordered**, **deterministic**, and **profile-driven**.

| Component                  | Responsibility                             |
| -------------------------- | ------------------------------------------ |
| `ProjectArtifactsSelector` | Selects profile-specific artifact pipeline |
| `ProjectArtifactsPort`     | Executes artifacts in defined order        |

> ProjectArtifactsPort is a composite executor —
it guarantees order, grouping, and profile isolation.

> Nothing is generated accidentally — every file is **architecturally intentional**.
> Execution order is defined by the selected profile;
the application merely **orchestrates** it.

---

## 🧬 Profiles — The Architecture Contract

Profiles externalize **what is generated** and **in which order**.

Example — `springboot-maven-java` profile pipeline:

```text
build-config
→ build-tool-files
→ ignore-rules
→ source-layout
→ app-config
→ main-source-entrypoint
→ test-source-entrypoint
→ sample-code (optional)
→ project-documentation
```

### Why Profiles Matter

Profiles are:

* ✔ Enforced **architecture standards**
* ✔ Reusable across **many teams & products**
* ✔ Extensible with **zero core refactor**
* ✔ The single source of truth for generation order

> Architecture governance, expressed as configuration — not tribal knowledge.

---

## 🧠 Architectural Takeaway

* **Domain** defines *capabilities*
* **Application** defines *orchestration & delivery*
* **Adapters** define *technology*
* **Profiles** define *architecture policy*

Nothing leaks.
Nothing is implicit.
Everything is intentional.

## 📐 Source Layout Enforcement

Two evolution paths:

### Standard

```
src/main/java/<basepkg>/
src/main/resources/
src/test/java/<basepkg>/
src/test/resources/
```

### Hexagonal (opt‑in evolution kit)

```
adapter/
  ├─ in/
  └─ out/
application/
domain/
bootstrap/
```

> Directories are treated as **domain objects** — guaranteed correctness.

---

## 📂 Resource Model — Better than "Just Files"

Every output is represented in the domain as:

| Type      | Domain Model              | Why                              |
| --------- | ------------------------- | -------------------------------- |
| Directory | `GeneratedDirectory`      | Structure is validated           |
| Text      | `GeneratedTextResource`   | Safe content models              |
| Binary    | `GeneratedBinaryResource` | Maven wrapper + future artifacts |

Supports: templates, non‑template content, binary, ZIP, future OCI.

---

## 🧪 Verified Architecture — Testing Strategy

| Test Type            | Ensures                                    |
| -------------------- | ------------------------------------------ |
| Unit                 | Rule enforcement inside domain/application |
| Integration (Spring) | Correct wiring + ordered pipelines         |
| E2E CLI tests        | Project structure validity post‑generation |
| Template tests       | Token correctness + UTF‑8 + placeholders   |

CI Quality:

* CodeQL security scanning
* Codecov coverage
* Contract test discipline
* **ArchUnit architectural guards — coming soon**

> Tests protect **architecture**, not just syntax.

---

## 🎯 What You Learn from This Repo

| Skill                    | How This Repo Teaches It            |
| ------------------------ | ----------------------------------- |
| Hexagonal mastery        | True isolation + enforced contracts |
| Maintainable scaffolding | Evolution paths from day zero       |
| Architecture automation  | "Governance as Code" patterns       |
| Multi‑stack enablement   | Add stacks without core edits       |
| Testing for architecture | Contract + pipeline validation      |

This is a **production reference architecture**, not a classroom demo.

---

## 🎮 Try It — CLI Delivery Adapter

```bash
java -jar codegen-blueprint-1.0.0.jar \
  --cli \
  springboot \
  --group-id io.github.blueprintplatform.samples \
  --artifact-id greeting-service \
  --name "Greeting Service" \
  --description "Sample Greeting Service built with hexagonal architecture" \
  --package-name io.github.blueprintplatform.samples.greeting \
  --layout hexagonal \
  --sample-code basic \
  --dependency web \
  --dependency data_jpa \
  --target-dir /path/to/output
```

```bash
cd greeting-service
./mvnw spring-boot:run
```

➡ Fully working REST sample included
GET `/api/v1/sample/greetings/default`

---

## 🔍 Architecture Execution Path (Mental Model)

```
CLI input
 ↓
ProjectBlueprint
 ↓
ProjectArtifactsSelector (selects profile)
 ↓
ProjectArtifactsPort (executes ordered ports)
 ↓
ProjectWriterPort (physical output)
```

> Architecture → compiled → executed.

---

## ⭐ Final Thoughts

**Executable Architecture** means:

* Architecture cannot drift accidentally
* Domain is always protected
* Tech can evolve independently
* Standards are repeatable across the organization

For teams who believe:

> "Architecture isn't a diagram — it's a behavior."

🚀 Happy generating with Codegen Blueprint! ✨
