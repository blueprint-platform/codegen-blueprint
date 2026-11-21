# Codegen Blueprint

**Architecture as an Executable Contract**

Start a Java/Maven Spring Boot project with package boundaries that Maven can enforce. Codegen Blueprint delivers the sources, the Maven wrapper and the ArchUnit tests selected for your layout. As the code evolves, a covered dependency can turn a successful compile into a failed build with a named architecture rule.

**Generate → Enforce → Prove**

[Generate your first project](docs/quickstart.md) · [Reproduce a boundary failure](docs/examples/boundary-proof.md) · [Explore the docs](docs/index.md)

[![The generated standard project passes, a controller-to-repository dependency fails its ArchUnit rule, and the restored project passes](docs/assets/proof-2.0.svg)](docs/examples/boundary-proof.md)

## Generate a project

From this checkout, use JDK 25 and Maven 3.9.16. The commands use the 2.0 development JAR. The [quickstart](docs/quickstart.md) is the example CI executes and also accepts a supplied JAR.

```bash
mvn -B -ntp clean package
OUTPUT_DIR="$(mktemp -d)"
java -jar target/codegen-blueprint-2.0.0-SNAPSHOT.jar --cli springboot \
  --group-id com.example --artifact-id greeting --name Greeting \
  --description 'Greeting sample' --package-name com.example.greeting \
  --layout standard --guardrails strict --sample-code basic \
  --target-dir "$OUTPUT_DIR"
cd "$OUTPUT_DIR/greeting"
sh ./mvnw -B -ntp clean verify
```

Expect a project directory and a sibling ZIP. The sample's Failsafe tests exercise a real HTTP server. A first Maven build needs access to the wrapper distribution and dependencies.

## Choose what the build enforces

| Layout | How to structure a feature |
| --- | --- |
| **Standard** | Put HTTP entry points in `controller`, orchestration in `service` and persistence access in `repository`. |
| **Hexagonal** | Put entry points in `adapter.in` and infrastructure in `adapter.out`. Define application ports and use cases in `application`, business types in `domain`, and wiring in `bootstrap`. |

| Guardrails | What gets generated |
| --- | --- |
| `none` | Project structure with no generated ArchUnit dependency or rules. |
| `basic` (default) | Layout-specific scope checks, core dependency restrictions and cycle checks between package families within a context. |
| `strict` | Basic coverage plus stronger layout-specific boundaries, including checks for REST signatures when MVC is present. |

[Inspect both generated layouts](docs/examples/generated-projects.md) · [Read the rulebook](docs/architecture/architecture-guardrails-rulebook.md)

## Prove that a boundary holds

In the generated Standard sample, add the following import and field to `GreetingController`.

```java
import com.example.greeting.bp.sample.repository.LoggingGreetingAuditRepository;

// Inside GreetingController
private LoggingGreetingAuditRepository __archViolation;
```

The field compiles. During `clean verify` the rule `controllers_must_not_depend_on_repositories` fails because the field type couples the HTTP controller directly to persistence. Removing the field and its import restores the passing build.

[Reproduce the proof](docs/examples/boundary-proof.md) · [Inspect the recorded results](docs/examples/proof-2.0.txt)

## Supported targets

The generator runs on **Spring Boot 4.2.0** and requires **Java 25**. It generates Maven projects for **Spring Boot 4.1 or 4.2**, each with **Java 21 or 25**. Defaults are **Boot 4.2 / Java 25**. Pass `--boot 4.1` or `--java 21` to choose an alternative. The CLI reports the exact stable patch written to the POM and explicitly reports when a connection failure selects the bundled reference.

[Compatibility](docs/compatibility.md) · [CLI reference](docs/cli.md) · [Architecture contract](docs/architecture/executable-architecture-contract.md) · [Release notes](docs/release-notes.md) · [Contributing](CONTRIBUTING.md)
