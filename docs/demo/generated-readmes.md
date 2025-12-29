# Generated Project README Previews

This document showcases **real, generated README outputs** produced by Codegen Blueprint.

It exists to make one thing explicit:

> **Codegen Blueprint does not only generate code — it generates an explicit, opinionated project-level architecture contract.**

The README files shown here are **not handwritten examples**.
They are the **exact `README.md` files generated into projects** created via the CLI.

The term *contract* in this document refers strictly to **project-level guarantees** —
layout, guardrails, and build-time behavior — as defined by the selected profile and options.

---

## 📑 Table of Contents

- [Why this matters](#why-this-matters)
- [What you are seeing](#what-you-are-seeing)
- [Hexagonal Architecture — Generated README](#hexagonal-architecture--generated-readme)
- [Standard (Layered) Architecture — Generated README](#standard-layered-architecture--generated-readme)
- [What this proves](#what-this-proves)
- [Relationship to Executable Architecture Proof](#relationship-to-executable-architecture-proof)
- [Summary](#summary)

---

## Why this matters

Most generators stop at code scaffolding.
Codegen Blueprint goes further by generating:

* a **clear project identity** (what was generated, how, and why)
* an explicit **architecture contract** (layout + optional guardrails)
* a **deterministic onboarding path** (how to build, run, and explore)

The generated README is therefore part of the **product output**, not auxiliary documentation.

---

## What you are seeing

Below are two generated README examples created from the **same engine**, using the **same guardrails mode (explicitly enabled)**, but with **different architectural models**:

| Variant   | Layout           | Guardrails Mode | Sample Code |
| --------- | ---------------- | --------------- | ----------- |
| Hexagonal | Ports & Adapters | strict          | basic       |
| Standard  | Layered          | strict          | basic       |

Both projects:

* were generated via the CLI
* compile and pass `mvn verify`
* include executable ArchUnit guardrails rules
* **fail the build deterministically when the enabled architectural boundaries are violated**

---

## Hexagonal Architecture — Generated README

This README was generated using:

```bash
--layout hexagonal --guardrails strict --sample-code basic
```

Key characteristics visible in the generated README:

* Explicit declaration of **hexagonal (ports & adapters) architecture**

* Clear explanation of **inbound / outbound boundaries**

* Strict guardrails **declared and enforced** via generated ArchUnit tests:

  * ports isolation
  * adapter direction rules
  * domain purity

* Exact location of generated ArchUnit rules

* Runnable, minimal sample demonstrating correct dependency flow

📄 **Full generated README:**

→ [Hexagonal Project README](./generated/hexagonal/README.md)

---

## Standard (Layered) Architecture — Generated README

This README was generated using:

```bash
--layout standard --guardrails strict --sample-code basic
```

Key characteristics visible in the generated README:

* Explicit declaration of **standard layered architecture**

* Clear explanation of layer responsibilities

* Strict guardrails **declared and enforced** via generated ArchUnit tests:

  * controller → service → repository dependency direction
  * domain purity
  * REST boundary isolation

* Deterministic failure behavior on violations

* Simple, readable sample designed for baseline understanding

📄 **Full generated README:**

→ [Standard Project README](./generated/standard/README.md)

---

## What this proves

These READMEs demonstrate that:

* Architecture is **declared**, not implied
* Enforcement is **explicit**, not hidden
* Generated projects are **self-explanatory**
* Onboarding does not rely on tribal knowledge

Most importantly:

> The generated README is part of the **generated project’s executable architecture contract**.

It documents exactly what the generator promised — and what the build will enforce.

---

## Relationship to Executable Architecture Proof

If you want to see **how these guarantees are enforced**, step by step, including:

* clean baseline builds
* intentional violations
* deterministic build failures

see:

→ **Executable Architecture Proof — Architecture Enforcement Walkthrough**

That document shows the **proof**.
The READMEs shown here represent the **resulting product**.

---

## Summary

Codegen Blueprint generates:

* architecture-aware code
* executable architectural guardrails (when enabled)
* **first-class project documentation**

The README is not an afterthought.
It is part of the generated system.

> **Architecture as a Product** starts with what the user sees first.
