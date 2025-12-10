# Architecture Enforcement Scope — Codegen Blueprint 1.0.0 GA

> This unified document defines what the **Codegen Blueprint engine enforces today (1.0.0 GA)** and what the **generated project guarantees at output** — a single reference point for architectural truth.

---

## 📚 Table of Contents

* [1 Purpose](#1-purpose)
* [2 Core Mental Model](#2-core-mental-model)
* [3 Engine Enforcement Guarantees (1.0.0 GA)](#3-engine-enforcement-guarantees-100-ga)
  * [3.1 Deterministic Project Layout](#31-deterministic-project-layout)
  * [3.2 Naming & Identity Enforcement](#32-naming--identity-enforcement)
  * [3.3 Spring Boot Minimal Runtime Baseline](#33-spring-boot-minimal-runtime-baseline)
  * [3.4 Test-Ready Project](#34-test-ready-project)
  * [3.5 Separation of Engine & Templates](#35-separation-of-engine--templates)
  * [3.6 Profile‑Driven Execution](#36-profile-driven-execution)
* [4 Generated Project Scope (Output Contract)](#4-generated-project-scope-output-contract)
* [5 Explicitly Not Enforced (Yet)](#5-explicitly-not-enforced-yet)
* [6 Intentional Scope Constraints](#6-intentional-scope-constraints)
* [7 Path Toward Executable Architecture](#7-path-toward-executable-architecture)
* [8 Review Guidance](#8-review-guidance)

---

## 1️⃣ Purpose

Ensure that:

* README **claims** match **actual engine guarantees**
* Teams get a **predictable**, **testable**, **clean** project every time
* Foundations are in place for **strict boundary enforcement** later

> 🧠 If we promise it, we enforce it.

---

## 2️⃣ Core Mental Model

| Concept        | Description                                          |
| -------------- | ---------------------------------------------------- |
| **Engine**     | CLI‑driven generator applying architectural profiles |
| **Profile**    | Defines language + build tool + architecture layout  |
| **Blueprints** | Artifact templates (POM, YAML, sources, docs)        |

📌 The engine today:

> Generates clean, production‑viable Spring Boot services — with architecture *prepared* for enforcement.

---

## 3️⃣ Engine Enforcement Guarantees (1.0.0 GA)

These are **strict contracts** validated through automated tests.

### 3.1 Deterministic Project Layout

Generated structure **must** follow:

```
<artifactId>/
 ├─ pom.xml
 ├─ src/main/java/<basePackage>/
 ├─ src/test/java/<basePackage>/
 ├─ src/main/resources/application.yml
 ├─ .gitignore
 └─ README.md
```

Always **single‑module**.

---

### 3.2 Naming & Identity Enforcement

Engine **normalizes + validates**:

* groupId
* artifactId
* package name
* application name

Main class rule:

```
<PascalCasedArtifact>Application
```

> ❌ Invalid identifiers → **fail fast**

---

### 3.3 Spring Boot Minimal Runtime Baseline

Project must:

* ✔ compile + run instantly
* ✔ use explicitly provided dependencies only
* ✔ bootstrap through SpringApplication.run(...)

📌 No accidental demo code.

---

### 3.4 Test Ready Project

Generated project must:

* contain test execution entrypoint via `@SpringBootTest`
* pass `mvn verify` right after generation

Testing is not optional.

---

### 3.5 Separation of Engine & Templates

Engine **does not depend on**:

* Spring
* File system
* Maven internals

All tech‑specific logic lives in **adapters + profiles**.

> Enables Gradle, Kotlin, Quarkus… with **zero** engine refactor.

---

### 3.6 Profile Driven Execution

```bash
java -jar codegen-blueprint.jar \
  --cli \
  springboot \
  --group-id com.acme \
  --artifact-id order-service \
  --name "Order Service" \
  --package-name com.acme.order \
  --layout hexagonal \
  --dependency web
```

Profile determines:

* templates
* structure
* tech behavior

---

## 4️⃣ Generated Project Scope (Output Contract)

### Standard Profile

```
springboot-maven-java
```

### Output requirements

```
<artifactId>/
 ├── pom.xml
 ├── src/main/java/<basePackage>/Application.java
 ├── src/test/java/<basePackage>/ApplicationTests.java
 ├── src/main/resources/application.yml
 ├── .gitignore
 └── README.md
```

### Architecture Option (OPT‑IN)

```bash
--layout hexagonal
```

Produces structured boundaries:

```
├── domain/        # business rules only
├── application/   # orchestrates ports
├── adapters/      # inbound + outbound
└── bootstrap/     # wiring + config
```

### Sample Code Option (OPT‑IN)

```bash
--sample-code basic
```

Provides ready‑to‑run teaching example:

```bash
GET /api/v1/sample/greetings/default
→ 200 OK
{
  "text": "Hello from hexagonal sample!"
}
```

Run instantly:

```bash
./mvnw spring-boot:run
```

---

## 5️⃣ Explicitly Not Enforced (Yet)

| Item                      | Reason                            |
| ------------------------- | --------------------------------- |
| Hexagonal by default      | Avoid adoption friction           |
| Policy engine             | Requires architectural DSL        |
| ArchUnit rules generation | Depends on next milestone         |
| Org‑wide governance       | Future platform-level enforcement |

> Architecture‑aware today → architecture‑policing tomorrow

---

## 6️⃣ Intentional Scope Constraints

* 🚫 No bloated features
* 🚫 No silent opinionated defaults
* 🎯 Precision > volume
* ♻️ Upgrade without core rewrites

> Narrow now → **massively scalable later**

---

## 7️⃣ Path Toward Executable Architecture

| Stage | Capability                   | Benefit                          |
| ----- | ---------------------------- | -------------------------------- |
| v1.1+ | Layout‑aware hex scaffolding | Real boundaries in code output   |
| v1.2+ | Auto‑architecture tests      | Prevent drift at compile/CI time |
| v1.3+ | Policy DSL                   | Architecture as CI quality gate  |
| v2.0  | Org profiles                 | Governance at organization scale |

---

## 8️⃣ Review Guidance

Every change touching architectural behavior must answer:

> ❓ Does this change **claim** enforcement?

If YES → update this document.
If NO → update README roadmap (only).

---

### Final Statement

**Codegen Blueprint 1.0.0 GA** generates:

* Clean & testable projects
* Architecture‑aware structure
* Predictable foundations for future enforcement

> **Executable Architecture begins here.** 🚀
