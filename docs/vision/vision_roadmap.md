## 🚀 Vision & Roadmap

> Architecture should **execute**, not merely be drawn.
> And it must remain **observable and verifiable** — even 6, 12, or 24 months later.

---

## 🌟 The Vision

**Blueprint Platform** exists to make architecture a **first‑class, executable product** — not a diagram, not a convention, not a guideline.

It stands on three pillars:

* **Architecture as a Product**
  Structure + guardrails delivered as a concrete, testable outcome
* **Capabilities via libraries + governance**
  Behavior is standardized and upgraded centrally — not copy‑pasted
* **Consistency that survives time and teams**
  Onboarding, upgrades, and standards remain intact despite churn

From **Day Zero to Production**, architecture stays **intentional**, **testable**, and **continuously evaluated**.

---

## 🧭 Roadmap Principles (Order Matters)

Blueprint evolves in **intentional layers** to protect its core promise and avoid premature surface expansion.

Each phase builds on **proven contracts and executable proof**, not assumptions.

1. **Strengthen the contract & proof**
   Determinism, architecture guardrails, reproducible evidence
2. **Add new delivery surfaces**
   CLI today → REST tomorrow — **without changing the core engine**
3. **Introduce capabilities via libraries + governance**
   Standardize behavior, don’t generate boilerplate
4. **Expand profiles cautiously**
   More stacks = more surface area → only after proof maturity

> 📌 Ordering is non‑negotiable.
> Capabilities and profiles come **after** architectural intent is proven executable.

---

## 🎯 Roadmap

### 🔹 Phase 1 — Architecture‑First Generation (1.0.0 GA)

This phase establishes the **executable architectural foundation**.

* Hexagonal / Standard (Layered) generation (opt‑in)
* Architecture guardrails via **generated ArchUnit checks**
  (`none | basic | strict`)
* CLI‑driven, profile‑based generation
  (Spring Boot · Maven · Java 21)
* Framework‑free domain core by construction
* End‑to‑end **buildable output** evaluated in CI
  (generated projects verified with `mvn verify`)

📌 **GA Objective** → zero‑drift foundations + executable proof

---

### 🔹 Phase 2 — New Delivery Surfaces (Planned)

This phase expands **access**, not responsibility.

The **core engine and domain surface remain unchanged**.

* REST inbound adapter
  (same engine, new entry point)
* Interactive onboarding / configuration UX
  (contract‑first, explicit intent capture)
* Safer defaults and clearer architectural signals

**Design intent (early):**

* Architecture dialects (Hexagonal / Standard variants) are selected **up‑front**
* Vocabulary choices become **explicit contract input**, not implicit convention
* UX guides teams to choose *one* dialect — not invent new ones

📌 Goal → broaden accessibility **without diluting architectural contracts**

---

### 🔹 Phase 3 — Capability‑Driven Architecture (Planned)

This phase operates at the **Blueprint Platform level**, not inside the generator.

Cross‑cutting concerns are **not generated as code**.
They are delivered as **versioned capabilities**, governed centrally.

`codegen‑blueprint` acts as the **entry point and wiring engine** — enabling adoption, configuration, and governance.

Planned capability areas:

* 🔐 Security (OAuth2 / Keycloak)
* 🔍 Observability (tracing, logs, metrics)
* 📡 Resilience (timeouts, retries, policies)
* 🏛️ Architecture policy packs
  (versioned guardrails applied consistently)
* 🔁 Generics‑aware OpenAPI clients
  (separate Blueprint module)

📌 Goal → consistent behavior and upgrades **in one place**, not per service

---

### 🔹 Phase 4 — Profile Expansion (Roadmap)

Profiles accelerate adoption but **increase surface area**.
They are introduced only after contracts and governance mature.

* Gradle profile
* Kotlin profile
* Quarkus and future stacks
* Visual UI — configure → generate → download
* Governance at scale (drift detection ideas)
* Opt‑in platform telemetry for architecture health

📌 Goal → expand stacks **after** proof and contracts are stable

---

## 🧩 Why This Matters

| Without Blueprint            | With Blueprint                     |
| ---------------------------- | ---------------------------------- |
| Architecture drifts silently | Guardrails make drift visible      |
| Boilerplate everywhere       | Capabilities via libraries         |
| Onboarding takes weeks       | Day‑zero structure + contracts     |
| Standards rely on discipline | Standards enforced by construction |

> 📌 Platform grows → projects stay clean → organizations remain consistent

---

**Blueprint Platform is not just code generation.**
It is **strategic architectural continuity** — explicit, observable, and executable.
