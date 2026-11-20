# Release readiness

The [architecture contract](../architecture/executable-architecture-contract.md) owns the stable behavior. Major releases may change that behavior with a migration guide. Minor releases add compatible capabilities. Patch releases correct defects within the supported line.

## 2.0 release checklist

- Complete the engine build on Java 21 and 25 and the four-cell generated verification matrix. Confirm the packaged JAR, generated HTTP and expected ArchUnit failures using reports.
- Run the documented quickstart against the exact candidate artifact in a fresh directory. Verify the generated Maven wrapper, JVM requirements, release metadata and checksum. Perform the Windows wrapper run before claiming Windows verification.
- Build and inspect the Pages artifact and validate internal links. Confirm actual GitHub Actions, CodeQL and Codecov results after the branch is eligible to run. Local execution cannot establish a hosted result.
- Finish the compatibility reference, [migration guide](../guides/migrate-to-2.0.md), [release notes](../releases/2.0.0.md) and proof evidence. Promote `2.0.0-SNAPSHOT` to `2.0.0` only in the reviewed release commit.
- Confirm the JAR name, manifest, checksum, source commit and intended `v2.0.0` tag refer to the same release. Preserve the published `v1.0.3` tag and artifacts.
- The maintainer authorizes release publication, Pages activation and social preview upload separately. The documentation workflow builds previews by default. Deployment requires an explicit dispatch on `main` and the repository’s Pages environment.
