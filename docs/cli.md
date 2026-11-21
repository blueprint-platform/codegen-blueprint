# CLI reference

Invoke the generator with `java -jar /path/to/codegen-blueprint.jar --cli springboot`. The packaged JAR reports its artifact version with `--cli --version`. Use `--cli springboot --help` for the parser’s current option list.

| Option | Required | Default | Accepted input |
| --- | --- | --- | --- |
| `--group-id` | Yes | | Maven group ID |
| `--artifact-id` | Yes | | Project artifact ID and output basename |
| `--name` | Yes | | Project name |
| `--description` | Yes | | Project description, 10–280 characters after whitespace normalization |
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

`--boot` identifies a minor line. For example, `--boot 4.1` is valid and `--boot 4.1.3` is rejected. The generator reads Maven Central metadata for the newest stable numeric patch on that line and checks the selected parent POM’s coordinates. This lookup does not validate the project’s dependency graph. The CLI reports `CENTRAL_LATEST` and the exact selected patch. That patch is written to the generated POM.

During either lookup (metadata or parent POM), DNS resolution errors, missing routes, refused connections and timeouts while awaiting response headers can trigger fallback. The warning identifies the request and connection failure, and the result reports `BUNDLED_FALLBACK`. Once the response arrives, HTTP errors, body read timeouts and oversized responses stop generation. Malformed data, missing or mismatched parents, TLS failures and interruption also stop generation. See [compatibility](compatibility.md) for bundled versions.

Online generation may select a newer patch on a later run. For the same generator, normalized inputs and resolved patch, the project tree and file contents repeat. Store the generated project in your own version control to retain a particular result. Wrapper downloads and Maven dependencies remain subject to the generated build’s network, mirror and cache settings.

## Dependency aliases

All aliases select artifacts under `org.springframework.boot`. The generated parent manages their versions.

| Alias | Artifact |
| --- | --- |
| `web` | `spring-boot-starter-webmvc` |
| `data_jpa` | `spring-boot-starter-data-jpa`, with `com.h2database:h2` added separately |
| `validation` | `spring-boot-starter-validation` |
| `actuator` | `spring-boot-starter-actuator` |
| `security` | `spring-boot-starter-security` |
| `devtools` | `spring-boot-devtools` |

The basic sample adds MVC when `web` is absent. Strict REST signature checks use the same effective MVC selection as the generated POM.

## Output and retry

Generation creates `<target-dir>/<artifact-id>` and `<target-dir>/<artifact-id>.zip`. An existing directory or ZIP is a conflict. If publication or cleanup fails, inspect the reported paths before retrying. Use another target when ownership of an existing path is uncertain. See the [delivery contract](architecture/executable-architecture-contract.md#delivery-and-reproducibility) for filesystem limits. On POSIX systems, `chmod +x mvnw` may be needed after extraction.
