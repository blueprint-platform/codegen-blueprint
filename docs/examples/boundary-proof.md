# Executable architecture proof

The proof runner generates samples with Strict guardrails. For each mutation, it checks a passing baseline, a specific ArchUnit failure after compilation, and a passing build after restoration. Every phase uses `sh ./mvnw -q -ntp clean verify`.

## Standard controller depends on a repository

The runner adds the repository import and the following field to `GreetingController`. Java accepts the field type. ArchUnit inspects the compiled class and reports the forbidden dependency.

```java
private LoggingGreetingAuditRepository __archViolation = null;
```

The first line identifies the failed assertion. The second identifies the source field and its target type. Both are from `logs/STD_violation.log`.

```text
[ERROR] io.github.blueprintplatform.greeting.architecture.archunit.StandardStrictLayerDependencyRulesTest.controllers_must_not_depend_on_repositories -- Time elapsed: 0.007 s <<< FAILURE!
Field <io.github.blueprintplatform.greeting.bp.sample.controller.GreetingController.__archViolation> has type <io.github.blueprintplatform.greeting.bp.sample.repository.LoggingGreetingAuditRepository> in (GreetingController.java:0)
```

## Run the proof

From the repository root, use JDK 25 and Maven 3.9.16. On macOS, select an installed JDK 25 with `export JAVA_HOME="$(/usr/libexec/java_home -v 25)"`.

```bash
mvn -B -ntp clean package
```

Run the controller-to-repository case.

```bash
PROOF_MODE=standard bash ci/proof/proof-runner.sh
```

To match the historical environment, run all five cases with the bundled Boot patch.

```bash
PROOF_MODE=full BOOT_VERSION=4.2 JAVA_VERSION=25 \
  EXPECT_BOOT_SOURCE=BUNDLED_FALLBACK bash ci/proof/proof-runner.sh
```

`EXPECT_BOOT_SOURCE=BUNDLED_FALLBACK` blocks the generator’s Central connection for the proof and verifies the reported resolution source. The generated Maven build still needs access to dependencies. To reuse a packaged generator, pass its absolute path as `CODEGEN_JAR`. To target Java 21, set `JAVA_VERSION=21`, point `JAVA_HOME` at JDK 21 and set `GENERATOR_JAVA` to the absolute path of the JDK 25 `java` executable.

## Five recorded scenarios

The [historical proof record](proof-2.0.txt) identifies the generator artifact by SHA-256 and records the JDK, Maven, exact Boot patch and assertion names. Both baselines passed. Each mutation produced its expected failure and each restoration passed Maven verification.

### Standard

- Renamed controller package — `StandardPackageSchemaSanityTest#each_standard_bounded_context_must_contain_required_canonical_families`
- Controller depends on a repository — `StandardStrictLayerDependencyRulesTest#controllers_must_not_depend_on_repositories`
- MVC response exposes a domain type — `StandardStrictRestBoundarySignatureIsolationTest#rest_controllers_must_not_expose_domain_types_in_signatures`

### Hexagonal

- Adapter depends on an application implementation — `HexagonalStrictPortsIsolationTest#adapters_must_not_depend_on_application_implementation`
- MVC response exposes a domain type — `HexagonalStrictRestBoundarySignatureIsolationTest#rest_controllers_must_not_expose_domain_types_in_signatures`

The runner checks the Surefire failure report and the named assertion in the Maven log before accepting an expected failure. Both REST cases exercise MVC enabled by the sample and types nested inside `ResponseEntity<List<…>>`.

## Inspect a run

Open the directory printed by the runner under `target/architecture-proof/`. Start with `proof-summary.txt`, then inspect `logs/` and `excerpts/` for the failing case. Set `KEEP_WORK_DIR=1` before running to retain the restored generated projects for source and report inspection. The proof runner also accepts `PROOF_OUTPUT_ROOT` for a different output location.

After a successful full run, replace `RUN_DIRECTORY` with the printed run directory name to produce a portable record:

```bash
python3 scripts/proof-record.py "target/architecture-proof/RUN_DIRECTORY"
```

See [compatibility](../compatibility.md#verification-coverage) for the broader matrix and the [rulebook](../architecture/architecture-guardrails-rulebook.md) for rule scope.

## Prove that Strict keeps the Basic boundaries

Run the continuity fixtures against the same packaged JAR. Use a fresh output path. The harness uses the bundled Boot patch and validates generated builds through the wrapper. Its green phases require the real sample HTTP report and architecture reports. Its red phases require each named assertion plus the fixture type in the Surefire failure.

```bash
python3 ci/proof/guardrail-continuity.py \
  target/codegen-blueprint-2.0.0-SNAPSHOT.jar target/guardrail-continuity \
  --boot 4.2 --java 25
```

| Fixture | Basic evidence | Strict evidence |
| --- | --- | --- |
| Standard service type references a controller | Service dependency assertion | Same service assertion |
| Standard `config ↔ service` cycle | Context-root cycle assertion | Same context-root cycle assertion alongside family-internal checks |
| Hexagonal application use case references a concrete adapter | Application direction assertion | Same application assertion |
| Hexagonal domain model references an adapter | Domain direction assertion | Domain purity assertion |
| Hexagonal application and adapter form a cycle | Context-root cycle assertion | Same context-root cycle assertion |
| Hexagonal adapter references a class directly in `application` | No separate port rule | Adapter implementation-isolation assertion |

The previous Strict templates accepted two compiling counterexamples under Boot 4.2 and Java 25: the Standard context-root cycle and the Hexagonal application-to-adapter edge. With the revised templates, the four continuity projects passed before mutation, reported all applicable assertions after mutation, and passed again after source restoration. The Standard cycle was reported as `Cycle detected: Slice config`; the Hexagonal application edge identified `Inward.next` referencing `adapter.out.Concrete`. The same four fixtures passed on Boot 4.1.3 / Java 21 and Boot 4.2.0 / Java 25 with bundled resolution.
