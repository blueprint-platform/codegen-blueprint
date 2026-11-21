# Architecture governance and AI collaboration

The human maintainer owns architectural intent, public contracts and merge decisions. AI can draft and verify changes within those boundaries. Architectural claims need observable build evidence, and each generated artifact needs an explicit purpose.

## Decision protocol

Record these five items before a nontrivial change can merge.

1. **Intent.** State the problem in terms of observable behavior.
2. **Boundary.** Identify the owning layer or profile and justify the placement.
3. **Contract.** Define inputs, outputs and failure behavior. Keep application models and adapter DTOs separate and route dependencies through ports.
4. **Proof.** Identify the test or generated rule that demonstrates the claim.
5. **Drift check.** Describe the regression the evidence will catch.

## Responsibility by layer

- **Domain** owns aggregates, value objects, policies and invariant violations. Keep frameworks, I/O, templating and delivery out of the domain.
- **Application** owns use-case sequencing and ports. It depends on the domain and delegates resolution, generation and publication.
- **Adapters** own technology-specific operations. Inbound adapters call input ports, and outbound adapters implement output ports.
- **Profiles** define artifact selection and order. Bootstrap binds the implementations and validates the configuration.

The [executable architecture contract](../architecture/executable-architecture-contract.md) describes the generation and enforcement boundaries. The [rulebook](../architecture/architecture-guardrails-rulebook.md) identifies the generated checks.

## Review an enforcement change

A negative proof should compile the mutated Java source, fail the named assertion in the emitted test, and pass after restoration. A dependency download or compiler error establishes a different failure. When a rule selector changes, cover both the intended offending type and any optional family the rule should allow to be empty. The [proof walkthrough](../examples/boundary-proof.md) shows the evidence to review before changing a claim in the README or rulebook.

## AI contributions

Follow established package boundaries and use names that express responsibilities. Keep changes small, explainable and reversible. Get a maintainer decision before introducing an architectural concept or renaming a public API. Keep frameworks out of core and preserve the existing guardrails.

## Merge review

- [ ] The intent, owning boundary and observable contract are explicit.
- [ ] Tests or generated rules exercise the claim.
- [ ] The change addresses the stated drift risk without introducing another one.
- [ ] README and reference documentation agree with observable behavior.
- [ ] The maintainer has reviewed any architectural decision introduced by tooling or framework choices.
