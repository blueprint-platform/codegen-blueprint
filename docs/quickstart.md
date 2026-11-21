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

## Try another target

Use `--boot 4.1` to select the other supported Spring Boot line. For Java 21 output, run the generator on JDK 25 with `--java 21`, then verify the generated project with JDK 21.

See [all CLI options](cli.md), or [reproduce a failing architecture rule](examples/boundary-proof.md).
