# Generate your first project

Use JDK **25** for this walkthrough. It can run the generator and build the default Java 25 project. Install Maven **3.9.16** to build the generator from this checkout. The generated project includes its own Maven wrapper. These commands use a POSIX shell.

From the repository root, build the snapshot once.

```bash
mvn -B -ntp clean package
```

Set the JAR path and generate into a fresh directory. The same command accepts a published 2.0 JAR when one is available.

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

The CLI prints the selected Boot patch and its source. The output contains the project directory and a sibling `greeting.zip`. Maven runs the generated ArchUnit tests and the greeting sample integration tests, including requests to a real HTTP server. The first build needs access to Maven dependencies and the wrapper distribution, or a cache that already contains them.

For a Java 21 generated project, pass `--java 21` and build with JDK 21. To use the older supported Boot line, pass `--boot 4.1`. See the [CLI reference](cli.md) for the other options.

To see a build fail on a specific architecture rule, follow the [green → red → green proof](examples/boundary-proof.md).
