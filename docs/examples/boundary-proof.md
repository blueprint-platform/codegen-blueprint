# Executable architecture proof

The proof starts from two generated Spring Boot projects. It runs Maven verification, introduces one compilable boundary violation at a time, checks the failing ArchUnit rule, restores the change and verifies again.

![Three verified states for a standard controller-to-repository dependency](../assets/proof-2.0.svg)

## Run the proof

Build the snapshot from the repository root, then run with JDK 25.

```bash
mvn -B -ntp clean package
CODEGEN_JAR="$PWD/target/codegen-blueprint-2.0.0-SNAPSHOT.jar" \
  bash ci/proof/proof-runner.sh
```

The runner prints its evidence directory under `target/architecture-proof/`. Set `PROOF_OUTPUT_ROOT` for another location. To reproduce the bundled-reference test run, set `EXPECT_BOOT_SOURCE=BUNDLED_FALLBACK`. That proof-only setting blocks the generator’s Central connection and checks the source reported by the CLI. The generated Maven build still needs access to dependencies.

## Recorded 2.0 result

The Java 25 / Boot 4.2 run passed both baselines, detected all five violations and passed each restored build. The recorded checkout produced a snapshot artifact. Its hash and the named assertions are available in the [portable proof record](proof-2.0.txt).

| Change introduced | Rule family that must fail |
| --- | --- |
| Hexagonal adapter depends on an application implementation | Ports isolation |
| Standard controller package renamed to a near-miss family | Package schema sanity |
| Standard controller depends on a repository | Layer dependency |
| Hexagonal MVC controller exposes a domain type | REST signature isolation |
| Standard MVC controller exposes a domain type | REST signature isolation |

The harness requires the named failure in the ArchUnit report and log. A compilation, download or unrelated test failure fails the proof itself. The REST cases rely on MVC added by the sample, exercising the generated guardrail without an explicit `web` alias.

See [compatibility](../compatibility.md#verification-coverage) for the broader matrix and the [rulebook](../architecture/architecture-guardrails-rulebook.md) for scope.
