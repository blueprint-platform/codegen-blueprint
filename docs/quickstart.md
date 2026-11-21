# Generate your first project

Use a POSIX shell, JDK **25** and Maven **3.9.16**. The first build needs access to Maven dependencies and the wrapper distribution, or a cache that already contains them.

From the repository root, build the snapshot once.

```bash
mvn -B -ntp clean package
```

Generate a Hexagonal greeting sample with Strict guardrails in a fresh directory. Set `CODEGEN_JAR` to an absolute path if you built the JAR elsewhere.

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

## Inspect the output

The CLI prints the exact Boot patch and how it was resolved. The output contains a `greeting` project directory and a sibling `greeting.zip`. In the generated project, open the reports from `clean verify`:

- `target/surefire-reports/` contains the generated ArchUnit results, including `HexagonalStrictRestBoundarySignatureIsolationTest`.
- `target/failsafe-reports/` contains `GreetingSampleHttpIT`, which sends requests to a running HTTP server.

## Select another target

Pass `--boot 4.1` for the other supported Boot line. For a Java 21 project, run the generator on JDK 25 with `--java 21`, then run the generated wrapper on JDK 21. See the [CLI reference](cli.md) for layout, dependency and sample options.

Follow the [boundary proof](examples/boundary-proof.md) to see what happens when a compiling source change crosses a checked boundary.
