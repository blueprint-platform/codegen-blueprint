# What We Explicitly Do **NOT** Guarantee

This document defines the **explicit non‑guarantees** of Codegen Blueprint.

It exists to prevent **incorrect expectations**, protect the architectural intent of the project, and make the scope of responsibility unambiguous.

> Codegen Blueprint is opinionated by design.
> What it does *not* do is as important as what it does.

If a behavior, capability, or outcome is **not explicitly guaranteed** in the authoritative contract set below, it must be assumed to be **out of scope**:

* **Executable Architecture Contract — 1.0.0 GA**
* **Release Discipline**
* The **README** sections that explicitly define **GA promise / scope** (and only those)

---

## Purpose of This Document

This document exists to:

* Prevent misuse of Codegen Blueprint
* Eliminate ambiguity about responsibility boundaries
* Avoid implicit promises created by assumptions
* Protect the project from being evaluated against goals it never claimed

This is **not** a legal disclaimer and **not** an abdication of responsibility.
It is a statement of **intentional design boundaries**.

---

## ❌ What Codegen Blueprint Does NOT Guarantee

### 1) Runtime Behavior or Production Correctness

Codegen Blueprint **does not guarantee**:

* Runtime correctness of generated applications
* Performance characteristics
* Scalability, resilience, or availability behavior
* Production‑grade configuration for any specific environment

The generator focuses on **architectural structure and boundaries**, not runtime behavior.

> A project that compiles and passes `mvn verify` is structurally valid —
> it is not automatically production‑ready.

---

### 2) Framework “Best Practices” or Idiomatic Usage Beyond the Contract

Codegen Blueprint **does not guarantee**:

* That generated code is the “best possible” or fully idiomatic implementation for a given framework
* That framework usage reflects every evolving community convention
* That generated APIs or service design follow a comprehensive framework style guide

Only what is **explicitly stated** in the contract set (GA contract + policies + explicit README GA scope) is considered guaranteed.

---

### 3) Universal Architecture Compatibility

Codegen Blueprint **does not guarantee**:

* Compatibility with every organizational architecture style
* Alignment with existing legacy codebases
* Compliance with undocumented or implicit internal company rules

Codegen Blueprint operates on **explicit, predefined architectural models**:

* `standard` (layered)
* `hexagonal` (ports & adapters)

These models define:

* the generated project structure
* the available architecture guardrails
* how architectural boundaries are evaluated at build time

If your organization’s architecture diverges from these models, Codegen Blueprint makes **no guarantee of compatibility or adaptation**.

---

### 4) Unlimited Dependency Freedom

Codegen Blueprint **intentionally does not guarantee**:

* Free‑form dependency injection via CLI
* Arbitrary third‑party library inclusion
* Automatic alignment with custom BOMs

Dependency aliases are **restricted by design** to:

* prevent dependency sprawl
* preserve architectural clarity
* maintain deterministic output

> Dependency freedom is a runtime decision.
> Architectural intent is a generation‑time decision.

---

### 5) Backward Compatibility Outside Declared Contracts

Codegen Blueprint **does not guarantee**:

* Backward compatibility across major versions
* Stability of experimental or pre‑GA behavior
* Compatibility for versions not explicitly listed as supported

Compatibility guarantees **begin at 1.0.0 GA** and apply **only within the declared version line**.

---

### 6) Automatic Migration or Upgrade Assistance

Codegen Blueprint **does not guarantee**:

* Automated migration between generated project versions
* Tooling for refactoring existing projects
* Upgrade paths for modified or manually altered output

Generated projects are **starting points**, not managed artifacts.

---

### 7) Absence of Required Architectural Decisions

Codegen Blueprint **does not remove the need** for:

* Architectural thinking
* Design trade‑off decisions
* Context‑specific judgment

The generator **encodes intent**, but it does not replace architects.

> Architecture as a Product does not mean architecture without responsibility.

---

### 8) Templates as a Public API or Supported Extension Surface

Templates are **not** considered a public API.

Codegen Blueprint **does not guarantee**:

* Stability of internal template structure
* Backward compatibility for custom template overrides
* Any supported or stable “template hook” / override mechanism

Only **generated output contracts** are protected — not template internals.

---

## Relationship to Other Policies

This document must be read together with:

* **Executable Architecture Contract — 1.0.0 GA** — the authoritative guarantee surface
* **Release Discipline** — versioning, compatibility, and change boundaries
* **SECURITY.md** — accepted security responsibilities
* **CONTRIBUTING.md** — contribution expectations and boundaries

Together, these documents define the **complete responsibility boundary** of the project.

---

## Final Statement

Codegen Blueprint is designed to be **deliberate, constrained, and guardrails‑driven**.

Its strength comes from:

* explicit guarantees
* intentional limitations
* architectural integrity over convenience

> If a feature is missing, it may be because adding it would weaken the architectural contract.

Understanding these non‑guarantees is essential to using Codegen Blueprint correctly.