# Compatibility and verification

The **generator** is compiled for and runs on Java **25**, with Spring Boot **4.2.0** internally. The **projects it generates** choose their own supported Java and Spring Boot target.

| Generated Spring Boot | Java 21 | Java 25 | Bundled reference patch |
| --- | --- | --- | --- |
| 4.1.x | Supported and verified | Supported and verified | `4.1.3` |
| 4.2.x | Supported and verified | Supported and verified (default) | `4.2.0` |

Maven **3.9.16** is the generator's CI reference and the version pinned by the generated wrapper. See the [CLI reference](cli.md) for all accepted selectors.

## How versions are selected

The requested **minor line** is a supported product boundary. At generation time, the generator selects the newest eligible stable patch visible on Maven Central and verifies its parent POM. That patch can change between runs.

A **bundled reference** is a patch with generated-project end-to-end evidence on both target JDKs. It changes with a Codegen release and is used only for eligible connection failures. Live latest resolution is not a claim that a newly published patch has already passed the full matrix.

The [patch resolution reference](cli.md#spring-boot-patch-resolution) explains warnings, hard failures and repeatability.

## Verification coverage

Local verification packaged the generator with JDK 25 and exercised the JAR across all four Boot/Java target pairs. The tested surface included both layouts, all guardrails modes, optional samples, explicit and implicit MVC, and a JPA/H2 plus Security combination. The generated Maven wrapper ran architecture checks, HTTP integration tests, a negative architecture proof and JVM classfile checks.

Earlier runs observed **4.1.3** and **4.2.0** from Maven Central. Temporary edge fixtures also verified Boot **4.1.0** on both JDKs by replacing only the generated parent version.

The [boundary proof](examples/boundary-proof.md) documents five Strict violations and separate Basic/Strict continuity fixtures. Continuity ran both layouts on Boot 4.1.3 / Java 21 and Boot 4.2.0 / Java 25 using bundled resolution.

**Scope of the claim:** The root build verifies the generator; generated-project builds verify selected output combinations. The matrix does not cover every patch or dependency alias combination. See [verification levels](maintainers/release-discipline.md#verification-levels) for CI, release and upstream checks.
