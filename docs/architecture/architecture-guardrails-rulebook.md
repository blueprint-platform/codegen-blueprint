# Guardrails rulebook

The generated tests live under `src/test/java/<base-package>/architecture/archunit/`. Surefire runs them during Maven verification. Both layouts support `none`, `basic` and `strict`. Strict is its own rule set. Verify the differences below when switching modes.

| Layout | Basic | Strict |
| --- | --- | --- |
| Standard | Controller↔repository isolation, controllers avoid domain services, domain avoids controller/service/repository, cycles across bounded-context roots | Direction rules for controller/service/repository, JDK/domain-only domain dependencies, controller DTO isolation, per-family package cycles, conditional REST signature isolation |
| Hexagonal | Application avoids adapters, bootstrap is a leaf, cycles across bounded-context roots | Adapter direction and port isolation, inbound adapter/DTO isolation, JDK/domain-only domain dependencies, bootstrap leaf, per-family package cycles, conditional REST signature isolation |

Both modes generate scope sanity, package schema and anchor drift checks. Scope sanity rejects an empty import. Schema checks canonical family roots within detected bounded contexts. Anchor checks look for near-miss names when expected families are missing. Optional selector scopes can be empty, so sample-free output can still pass. Package markers establish structure and do not certify that every application class is in the intended package.

Strict REST signature checks are generated when MVC is present, whether selected with `web` or enabled by the basic sample. These checks detect domain types in covered controller method signatures.

`none` retains the selected layout while omitting ArchUnit. Sample integration tests remain independent of the guardrails setting.

Use the [executable proof](../demo/executable-architecture-proof.md) to reproduce a failing rule. See the [architecture contract](executable-architecture-contract.md) for scope and limits.
