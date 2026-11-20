# CLI reference

Invoke the generator with `java -jar /path/to/codegen-blueprint.jar --cli springboot`. The packaged JAR reports its artifact version with `--cli --version`. Use `--cli springboot --help` for the parser’s current option list.

| Option | Required | Default | Accepted input |
| --- | --- | --- | --- |
| `--group-id` | Yes | | Maven group ID |
| `--artifact-id` | Yes | | Project artifact ID and output basename |
| `--name` | Yes | | Project name |
| `--description` | Yes | | Project description, 10–280 characters |
| `--package-name` | Yes | | Java base package |
| `--build-tool` | | `maven` | `maven` |
| `--language` | | `java` | `java` |
| `--java` | | `25` | `21`, `25` |
| `--boot` | | `4.2` | `4.1`, `4.2` |
| `--layout` | | `standard` | `standard`, `hexagonal` |
| `--guardrails` | | `basic` | `none`, `basic`, `strict` |
| `--sample-code` | | `none` | `none`, `basic` |
| `--dependency` | | | Repeat an alias from the list below |
| `--target-dir` | | `.` | Parent directory of the generated project and ZIP |

## Spring Boot patch resolution

`--boot` identifies a minor line. For example, `--boot 4.1` is valid and `--boot 4.1.3` is rejected. The generator checks Maven Central for the newest stable patch on that line and verifies its parent POM. The CLI reports `CENTRAL_LATEST` and the exact selected patch. That patch is written to the generated POM.

DNS, routing, connection refusal and timeout before receiving an HTTP response can trigger automatic fallback. The warning includes the lookup phase and failure category, and the result reports `BUNDLED_FALLBACK`. HTTP errors, missing parent artifacts, malformed data and TLS failures stop generation. See [compatibility](compatibility.md) for bundled versions.

Online generation may select a newer patch on a later run. For the same generator, normalized inputs and resolved patch, the project tree and file contents repeat. Store the generated project in your own version control to retain a particular result. Wrapper downloads and Maven dependencies remain subject to the generated build’s network, mirror and cache settings.

## Dependency aliases

`web` enables MVC, `data_jpa` adds JPA with H2, and `validation`, `actuator`, `security`, `devtools` select their Spring Boot capabilities. The basic sample also enables MVC when `web` is absent. Boot manages the starter dependencies in the generated build.

## Output and retry

Generation creates `<target-dir>/<artifact-id>` and `<target-dir>/<artifact-id>.zip`. An existing directory or ZIP is a conflict. Inspect reported leftover paths if a write or cleanup fails, then select another target or clear only the failed attempt’s files. On POSIX systems, `chmod +x mvnw` may be needed after extraction.
