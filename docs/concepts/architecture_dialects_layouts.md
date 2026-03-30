# Architecture Dialects — Hexagonal & Standard

This document defines the **official architecture dialects** supported conceptually by the Blueprint Platform.

A **dialect** is a *canonical vocabulary choice* for package names and structural tokens **within the same architectural model**.

> Dialects do **not** change architectural rules.
> They change **how the same rules are named and expressed**.

Once a dialect is selected:

* Package tokens are **fixed**
* Guardrails, source layout, and generated contracts **speak the same vocabulary**
* Renaming tokens is an **intentional contract change**, not a refactor

---

## 1. Hexagonal Architecture Dialects

Hexagonal architecture enforces **ports & adapters isolation**.
Dialects only affect **naming**, not dependency direction.

### Dialect H1 — Clean / Singular (Canonical)

**Intent:** Minimal, explicit, architecture‑first vocabulary.

```
<base>/
├─ adapter/
│  ├─ in/
│  └─ out/
├─ application/
│  ├─ port/
│  │  ├─ in/
│  │  └─ out/
│  └─ usecase/
├─ domain/
│  ├─ model/
│  ├─ service/
│  └─ port/
│     ├─ in/
│     └─ out/
└─ bootstrap/
```

**Application implementation token**

* Default: `usecase/`
* Alternative: `handler/`

---

### Dialect H2 — Plural + Conventional Words

**Intent:** Corporate familiarity with strict plural consistency.

```
<base>/
├─ adapters/
│  ├─ in/
│  └─ out/
├─ application/
│  ├─ ports/
│  │  ├─ in/
│  │  └─ out/
│  └─ usecases/
├─ domain/
│  ├─ model/
│  ├─ services/
│  └─ ports/
│     ├─ in/
│     └─ out/
└─ bootstrap/
```

**Application implementation token**

* Default: `usecases/`
* Alternative: `handlers/`

Rule: *If plural is chosen, all structural families follow plural naming.*

---

### Dialect H3 — Application Service + Infrastructure (DDD‑Oriented)

**Intent:** Align with DDD language while preserving hexagonal rules.

```
<base>/
├─ adapter/
│  ├─ in/
│  └─ out/
├─ application/
│  ├─ port/
│  │  ├─ in/
│  │  └─ out/
│  └─ service/
├─ domain/
│  ├─ model/
│  ├─ service/
│  └─ port/
│     ├─ in/
│     └─ out/
└─ infrastructure/
```

**Application implementation token**

* Default: `service/` *(application service semantics)*
* Alternative: `handler/`

Note: `infrastructure/` replaces `bootstrap/` as a more conventional wiring concept.

---

## 2. Standard (Layered) Architecture Dialects

Standard architecture enforces **layered dependency direction**.
Dialects adjust **terminology**, not layering semantics.

### Dialect S1 — Clean / Singular (Canonical)

**Intent:** Classic Spring‑style layered architecture.

```
<base>/
├─ controller/
│  └─ dto/
├─ service/
├─ repository/
├─ domain/
│  ├─ model/
│  └─ service/
└─ config/
```

**Application implementation token**

* Default: `service/`
* Alternative: `usecase/`

---

### Dialect S2 — Plural + Conventional Words

**Intent:** Enterprise conventions with pluralized layers.

```
<base>/
├─ controllers/
│  └─ dto/
├─ services/
├─ repositories/
├─ domain/
│  ├─ model/
│  └─ services/
└─ config/
```

Rule:

* Layer names are pluralized
* `domain` typically remains singular to avoid semantic confusion

**Application implementation token**

* Default: `services/`
* Alternative: `usecases/`

---

### Dialect S3 — API / Application / Domain / Infrastructure

**Intent:** DDD‑influenced layered architecture with explicit responsibility boundaries.

```
<base>/
├─ api/                 # replaces controller
│  └─ dto/
├─ application/         # application logic lives here
│  ├─ usecase/          # or handler/
├─ domain/
│  ├─ model/
│  └─ service/
└─ infrastructure/
   ├─ persistence/
   └─ config/
```

Clarification:

* `api/` **replaces** `controller/` and represents the delivery layer
* Repositories are treated as **infrastructure concerns**, not a primary layer

**Application implementation token**

* Default: `usecase/`
* Alternative: `handler/`

---

## 3. Dialect Rules (Binding)

* A dialect is selected **explicitly**
* Once selected, vocabulary tokens are **fixed**
* Guardrails, source layout, and documentation share the same dialect
* Renaming a token is a **contract change**, not a cosmetic refactor

> Dialects provide flexibility **without opening Pandora’s box**.
> The set is intentionally curated and finite.

---

## 4. Why Dialects Exist

* Teams disagree on naming, not on architectural intent
* Naming drift causes more friction than structural rules
* Dialects make architectural intent **explicit at generation time**

> Architecture remains executable.
> Vocabulary becomes intentional.

---

**Architecture as a Product**
*Explicit. Observable. Executable.*
