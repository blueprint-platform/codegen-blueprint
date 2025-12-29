# 🧩 Pull Request Template

Thank you for contributing to **codegen-blueprint** 🙌
This project is architecture-first and contract-driven (profiles, ports, and generated guardrails), so please keep PRs **small, focused, and explicit**.

---

## 🎯 Summary

> What does this PR change, and **why**? One short paragraph.

Example:

> Refactors artifact pipeline selection to make profile resolution explicit and deterministic, without changing domain behavior.

---

## 📦 Changes

Briefly list the concrete changes:

* Domain model changes (aggregates, value objects, policies)
* Application orchestration changes (use cases, ports)
* Adapter changes (filesystem, templating, profiles)
* CI / build / tooling updates
* Documentation updates

---

## 🧠 Architectural Impact

Explain the architectural intent (if applicable):

* Does this affect **domain purity**?
* Does it introduce or modify a **port / adapter boundary**?
* Does it change **profile behavior** or artifact ordering?
* Does it affect **architecture guardrails (generation, scope, or behavior)?**?

If **none**, say so explicitly.

---

## 🧪 Validation

How was this change validated?

* Unit tests added or updated
* Integration tests (generated project build & verification)
* Manual verification (CLI / generated output)
* CI matrix impact (if any)

---

## ✅ Checklist

* [ ] Scope is minimal and focused
* [ ] Domain layer remains framework-agnostic
* [ ] Application layer contains no business logic (or domain rules)
* [ ] Ports express intent, not implementation
* [ ] Adapters contain all IO / framework specifics
* [ ] Build passes locally: `mvn -q -ntp clean verify`
* [ ] Tests added or updated where appropriate
* [ ] Documentation updated (README / docs) if behavior changed
* [ ] No unintended changes to generated output
* [ ] Linked issue (if applicable)

---

## 🧾 Metadata

**Type:** `feature` / `bugfix` / `docs` / `refactor` / `chore` / `test` / `ci`
**Layer:** `domain` / `application` / `adapter` / `ci` / `docs`
**Target Release:** (optional) e.g. `v1.0.0`, `v1.0.1`

---

> 💡 *Notes*
>
> * This project values **architectural clarity over convenience**.
> * Large or mixed-scope PRs are likely to be requested to split.
> * If unsure about scope or direction, open a discussion before coding.
> * Architectural changes should be observable through generated output or build-time feedback.
