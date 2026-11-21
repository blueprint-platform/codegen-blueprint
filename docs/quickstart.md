# Quickstart: generate and verify a project

Use a POSIX shell, **JDK 25** and **Maven 3.9.16**. The initial build requires access to Maven dependencies and the wrapper distribution, or a populated cache.

## 1. Package the generator

From the repository root:

```bash
mvn -B -ntp clean package
```

## 2. Generate a Hexagonal sample

The command below creates a fresh output directory, generates a project with Strict architecture guardrails and runs the project's own Maven verification. Set `CODEGEN_JAR` to an absolute path if the JAR is stored outside this checkout.

<!-- quickstart:verified -->
```bash
CODEGEN_JAR="${CODEGEN_JAR:-$PWD/target/codegen-blueprint-2.0.0-SNAPSHOT.jar}"
OUTPUT_DIR="$(mktemp -d)"

java -jar "$CODEGEN_JAR" --cli springboot \
  --group-id com.example \
  --artifact-id greeting \
  --name Greeting \
  --description "Greeting sample with executable architecture" \
  --package-name com.example.greeting \
  --layout hexagonal \
  --guardrails strict \
  --sample-code basic \
  --target-dir "$OUTPUT_DIR"

cd "$OUTPUT_DIR/greeting"
chmod +x mvnw
./mvnw -B -ntp clean verify
```
<!-- /quickstart:verified -->

## 3. Inspect the result

Generation creates the `greeting/` project and a sibling `greeting.zip`. The CLI reports the selected Boot patch and its resolution source.

| Report | Evidence |
| --- | --- |
| `target/surefire-reports/` | Generated ArchUnit checks, including `HexagonalStrictRestBoundarySignatureIsolationTest` |
| `target/failsafe-reports/` | `GreetingSampleHttpIT`, which makes requests to a running HTTP server |

### Trace the output from the build to the source

These are selected paths in the generated directory. The full tree also contains the application entrypoint, configuration and tests.

```text
greeting/
├── pom.xml
├── .mvn/wrapper/maven-wrapper.properties
├── src/main/java/com/example/greeting/bp/sample/
│   ├── adapter/in/rest/GreetingController.java
│   ├── application/port/in/GetGreetingPort.java
│   └── domain/port/out/GreetingAuditPort.java
└── src/test/java/com/example/greeting/architecture/archunit/
    └── HexagonalStrictRestBoundarySignatureIsolationTest.java
```

The parent in `pom.xml` is the exact Boot patch reported by the CLI. The wrapper version is pinned separately in `maven-wrapper.properties`. The generated README records the selected layout and mode.

Follow `GreetingController` to the input port, then open the rule under `architecture/archunit` to see what the build checks. The command omits `--dependency web`. The basic sample adds MVC, which activates the Strict REST rule as part of the same dependency decision.

To call the same sample manually, start it in one terminal from the generated directory:

```bash
sh ./mvnw spring-boot:run
```

From another terminal:

```bash
curl -fsS http://localhost:8080/api/v1/sample/greetings/default
curl -fsS 'http://localhost:8080/api/v1/sample/greetings?name=John'
```

Stop the app in the first terminal with Ctrl-C. The verified build already exercises HTTP through the sample integration test.

## Try another target

Use `--boot 4.1` to select the other supported Spring Boot line. For Java 21 output, run the generator on JDK 25 with `--java 21`, then verify the generated project with JDK 21.

See [all CLI options](cli.md), or [reproduce a failing architecture rule](examples/boundary-proof.md).
