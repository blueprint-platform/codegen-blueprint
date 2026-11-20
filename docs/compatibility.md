# Compatibility

The generator runs on Java **21** or **25**. Its executable JAR is compiled for Java 21 and uses Spring Boot **4.1.3** internally. Generated projects choose their own Boot patch independently.

| Generated Spring Boot line | Java 21 | Java 25 | Bundled reference |
| --- | --- | --- | --- |
| 4.1.x | Supported and verified | Supported and verified | 4.1.3 |
| 4.2.x | Supported and verified | Supported and verified · default | 4.2.0 |

The generated build uses Maven and includes a Maven **3.9.16** wrapper. The generator build uses Maven 3.9.16 as its CI reference. The public CLI accepts Boot minor selectors only. Boot 3.x and 4.0.x and Java versions other than 21 and 25 are outside the generation contract.

## How versions are selected

The supported line is a product boundary. The newest stable patch visible in Maven Central can change independently of Codegen releases. A new patch can be selected before its candidate build has passed. A **bundled reference** is a patch with generated-project E2E evidence on both JDKs. It is used only for eligible connection failures and changes with a Codegen release. The [CLI reference](cli.md#spring-boot-patch-resolution) explains failures and reproducibility.

## Verification coverage

The 2.0 local verification ran the generator tests on both JDKs and generated real projects from the same JAR across all four Boot/JDK pairs. The matrix covered both layouts, all guardrails modes, optional samples, explicit and implicit MVC, and a representative dependency combination with JPA/H2 and Security. Wrapper verification included real HTTP, an ArchUnit failure round trip and JVM classfile checks. Live Central selected **4.1.3** and **4.2.0** during the recorded run. The 4.1.0 edge build passed with both JDKs after substituting only the parent in a temporary generated project.

The [proof record](examples/boundary-proof.md) describes what was actually executed. The root build validates the generator. The separate generated-project build validates its output. The matrix samples the supported surface without claiming every patch or alias combination has been exercised.

See [verification levels](maintainers/release-discipline.md#verification-levels) for the blocking matrix, release checks and scheduled upstream candidates.
