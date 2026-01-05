# Release Discipline

This document defines the **release and compatibility discipline** of Codegen Blueprint.
It explains what each version line means, what is guaranteed, and how the project evolves **without breaking architectural promises**.

Codegen Blueprint treats releases as **product contracts**, not marketing milestones.

---

## Core principles

* Releases communicate **guarantees**, not feature volume.
* Compatibility is **intentional and explicit**.
* Architectural integrity takes precedence over speed.
* Breaking changes are allowed only when clearly signaled.

> If a guarantee is not written here (or in the GA contract), it is **not guaranteed**.

---

## Source of truth

The project’s contract surface is defined by the following documents, in order:

1. **Executable Architecture Contract — 1.0.0 GA**

  * Authoritative guardrails + generated-output guarantee surface.
2. **Release Discipline** (this document)

  * Authoritative versioning + compatibility discipline.
3. **What We Explicitly Do NOT Guarantee**

  * Authoritative non-guarantees and responsibility boundaries.

**Conflict rule:** If anything here is ambiguous or appears to conflict, the **GA contract wins**.

---

## Versioning model

Codegen Blueprint follows a **SemVer-inspired** model, with additional architectural meaning:

```
MAJOR.MINOR.PATCH
```

Where:

* **MAJOR** — Deliberate architectural/contract reset (breaking change).
* **MINOR** — Backward-compatible capability expansion (opt-in).
* **PATCH** — Bug fixes and non-behavioral corrections (no intentional contract expansion).

---

## 1.0.0 — General Availability (GA)

Version **1.0.0** marks the point where Codegen Blueprint becomes a **contractually stable product**, not an experiment.

From this version onward:

* Public behavior is intentional.
* Guarantees are explicit.
* Compatibility expectations begin.

> Anything prior to 1.0.0 exists for historical context only.

### What 1.0.0 guarantees

These guarantees define the **minimum bar** that will not regress within the **1.x** line (as defined by the GA contract and this policy):

* **Deterministic project generation**

  * Same input → same output (structure + files).
* **Framework-free domain core** by construction.
* Supported architecture layouts:

  * `standard` (layered)
  * `hexagonal` (ports & adapters)
* **Executable architecture guardrails** (opt-in, mode-based):

  * Generated ArchUnit tests (`basic`, `strict`) with explicit opt-out.
* Generated projects pass **`mvn verify`** on first run (within the declared supported matrix).
* Profile-driven stack selection (not ad-hoc flags).

> The authoritative GA guarantee surface is defined in **Executable Architecture Contract — 1.0.0 GA**.

---

## 1.0.x — Patch releases

Patch releases:

* Fix bugs.
* Correct incorrect or unintended behavior.
* Improve documentation.
* Stabilize internals.

Patch releases **do not**:

* Change CLI contracts in breaking ways.
* Modify generated project structure in breaking ways.
* Introduce breaking changes to the GA contract surface.

### Compatibility expectation (within 1.0.x)

If a project was generated under **1.0.0** and was valid under the GA contract, it is expected to remain valid under any **1.0.x** release.

**Allowed exception (still PATCH):**

* A fix that corrects behavior that was **objectively incorrect, unsafe, or security-relevant**.
* In rare cases, this can **tighten guardrails** when the previous behavior was a bug (e.g., a rule not being applied correctly).

Patch releases may remove accidental loopholes; they do not redefine the intended contract or introduce new architectural intent.

---

## 1.1.x — Minor releases

Minor releases introduce **new capabilities without breaking existing guarantees**.

Examples of allowed changes:

* New CLI options (opt-in).
* New profiles or artifacts.
* Additional guardrails modes.
* Extended template coverage.
* Support for newer platform versions (opt-in).

Minor releases **must not**:

* Break existing layouts.
* Change default behavior silently.
* Invalidate existing generated projects that remain within the previously declared supported matrix.

> Upgrading from 1.0.x → 1.1.x should be safe for existing users unless they explicitly opt into new capabilities.

---

## 2.0.0 — Major releases

A major release indicates a **deliberate contract reset**.

This may include:

* Architectural model changes.
* CLI contract restructuring.
* Breaking changes to generated output.
* Guardrails semantics changes.

Major releases:

* Are announced clearly.
* Are documented explicitly.
* Provide migration guidance when feasible.

> Breaking architecture silently is never acceptable.
> Breaking it explicitly — with intent — sometimes is.

---

## Compatibility philosophy

Compatibility is evaluated at three levels:

1. **Generator behavior** (CLI semantics, deterministic behavior).
2. **Generated project structure** (files, layout, artifact set).
3. **Executable architecture guarantees** (guardrails produced and evaluated at build time).

A release is considered compatible only if **all three remain valid** within the declared contract.

---

## Supported platform versions

Codegen Blueprint distinguishes **generator runtime** from **generated project targets**.

### Generator runtime baseline (this repository)

* **Java 21** is the runtime baseline for the generator.

### Generated project baseline (GA contract scope)

Generated projects are supported within the declared matrix:

* **Java:** 21 (GA target)
* **Spring Boot:** 3.4 and 3.5 (with 3.5 as default)
* **Build tool:** Maven 3.9+

### Forward-compat signals (not a contract)

Newer JDKs (e.g., **Java 25**) may be used in CI as a **forward-compat smoke signal**.

* This provides early warning.
* It does **not** expand the supported/guaranteed matrix unless explicitly stated in a release.

---

## Final note

Release discipline exists to protect users from ambiguity.

Codegen Blueprint prefers:

* Fewer releases
* Clear contracts
* Predictable evolution

Over fast iteration with undefined consequences.

> Architecture is not only generated — it is **versioned, protected, and evaluated through executable guardrails**.