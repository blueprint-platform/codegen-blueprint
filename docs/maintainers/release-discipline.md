# Release readiness

The [architecture contract](../architecture/executable-architecture-contract.md) owns stable behavior. Record intentional breaking changes in release notes. Minor releases add compatible capabilities. Patch releases correct defects within the supported line.

## Verification levels

| Level | When | Evidence |
| --- | --- | --- |
| Quick | During development | Run `mvn -B -ntp test` on JDK 21 for unit tests and engine ArchUnit. |
| Standard | Pull requests and main | Engine `clean verify` on Java 21 and 25. Four generated cells use the same Java 21 JAR, run Maven and exercise real HTTP. A compact negative proof and the documented quickstart also run. |
| Full | Manual Build & Test dispatch on the release candidate ref | Eight generated scenarios and five negative cases per cell, Boot 4.1.0 edge fixtures on both JDKs, and fresh wrapper and dependency caches on both Boot 4.2 cells. |
| Upstream | Weekly | A single JAR checks live Central latest patches across the four supported Boot/Java pairs. Candidate failures surface in the failing jobs. Bundled references retain their own evidence. |

| Standard Boot / Java | Scenarios |
| --- | --- |
| 4.1 / 21 | Standard basic HTTP with explicit MVC |
| 4.1 / 25 | Hexagonal strict HTTP with implicit MVC |
| 4.2 / 21 | Hexagonal strict HTTP with JPA/H2 and Security |
| 4.2 / 25 | Standard strict HTTP, standard none without sample, hexagonal strict without sample, controller-to-repository green → red → green |

Standard runs six matrix builds, three proof builds and one quickstart build. Full runs 80 baseline matrix and proof builds, four edge builds, two cold builds and one quickstart. Build & Test runs both engine JDKs separately.

For a local cell, first run `mvn -B -ntp clean verify` on JDK 21 and retain the packaged JAR. Select the cell's build JDK with `JAVA_HOME` and prepend its `bin` to `PATH`. Use an absolute JAR path outside `target` if running another Maven clean in between.

```bash
BOOT_VERSION=4.2 JAVA_VERSION=25 bash ci/verify-generated.sh standard
BOOT_VERSION=4.2 JAVA_VERSION=25 bash ci/verify-generated.sh full
```

Run the other cells one at a time on a laptop. Set `CODEGEN_JAR` to reuse the artifact and `GENERATOR_JAVA` to run it on the other JVM. For the five negative cases alone, run `bash ci/proof/proof-runner.sh`. Set `PROOF_MODE=standard` for the compact proof. When changing Basic or Strict rules, also run the [continuity fixtures](../examples/boundary-proof.md#prove-that-strict-keeps-the-basic-boundaries) to check both modes in both layouts. The fixtures are a separate local check.

## 2.0 release checklist

- Dispatch Full on the candidate ref. Inspect wrapper, HTTP and ArchUnit reports. Review the 4.1.0 edge fixtures and the live upstream jobs. The edge fixtures alter only the parent in a temporary generated project.
- Run the [quickstart](../quickstart.md) against the exact release candidate with fresh output. Perform the Windows wrapper run before claiming Windows verification.
- Inspect the Pages artifact, CodeQL and Codecov results. Local runs establish local evidence.
- Finish the compatibility reference, [release notes](../release-notes.md) and proof evidence. Promote `2.0.0-SNAPSHOT` to `2.0.0` in the reviewed release commit.
- Confirm the JAR name, manifest, checksum, source commit and intended `v2.0.0` tag refer to the same release. Preserve the historical `v1.0.3` tag and artifacts.
- The maintainer authorizes release publication, Pages activation and social preview upload separately. Pages deployment requires an explicit dispatch on `main` and the repository’s Pages environment.
