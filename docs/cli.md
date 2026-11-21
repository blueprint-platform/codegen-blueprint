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

## How input becomes a blueprint

Picocli checks required options and converts selectors and aliases. The application then validates the request and the selected profile's metadata before contacting Maven Central. The group ID and base package normalize to lowercase dot-separated segments. The artifact ID normalizes to lowercase and uses hyphens for spaces and underscores. It must begin with a letter and determines both output names. Use a Java-safe base package such as `com.example.greeting`.

The Maven profile treats the name and description as literal XML text. Values that include Maven interpolation syntax such as `${property}` or characters forbidden in XML fail metadata validation. The POM template escapes accepted text. Error messages identify the failing option or value policy, so fix input errors before investigating Central or the filesystem.

## Spring Boot patch resolution

Pass a minor line such as `--boot 4.1`. An exact patch such as `--boot 4.1.3` is rejected. The generator reads Maven Central metadata, selects the newest stable numeric patch on that line and checks the parent POM’s coordinates. It pins the patch in the generated POM and reports `CENTRAL_LATEST`. Maven resolves the dependency graph when building the generated project.

| Lookup outcome | Generator behavior |
| --- | --- |
| Stable patch selected and parent coordinates match | Pins the patch and reports `CENTRAL_LATEST`. |
| DNS failure, missing route, refused connection or timeout awaiting response headers | Warns with the phase and underlying cause. Pins the bundled reference for the requested line and reports `BUNDLED_FALLBACK`. |
| HTTP error, oversized or stalled body, malformed metadata, missing or mismatched parent, TLS failure or interruption | Fails generation before rendering or publication. |

Numeric ordering matters when Central lists patches such as `4.1.9` and `4.1.10`. The resolver compares the patch numbers and excludes prerelease and snapshot versions. If Central responds but cannot establish a valid parent, using a bundled patch would hide a different failure. [Resolution source and verified patches](compatibility.md#how-versions-are-selected)

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

For example, pass `--dependency web --dependency actuator` to select MVC and Actuator. The generated POM also includes a core Spring Boot starter and a test starter. Enabled guardrails add test-scope ArchUnit. The sample adds MVC test support and, when Security is selected, Security test support.

The basic sample adds MVC when `web` is absent. Strict REST signature checks use the same effective MVC selection as the generated POM.

## Output and retry

Generation creates `<target-dir>/<artifact-id>` and `<target-dir>/<artifact-id>.zip`. Either existing path is a conflict, including a file or link at the proposed project path. Pick a different target or artifact ID for another generation. When a failed attempt reports cleanup uncertainty, inspect the paths before retrying. A fresh target avoids claiming any path left by that attempt. See the [delivery contract](architecture/executable-architecture-contract.md#delivery-and-reproducibility) for filesystem limits. On POSIX systems, `chmod +x mvnw` may be needed after extraction.
