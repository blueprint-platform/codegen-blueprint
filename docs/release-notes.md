# 2.0.0 release notes

Release candidate documentation for the current `2.0.0-SNAPSHOT` branch. The 2.0.0 binary has not been published.

- Generate Java/Maven projects for Spring Boot 4.1 or 4.2 using Java 21 or 25. The default is Boot 4.2 / Java 25.
- Resolve stable Boot patches at generation time. The generated POM pins the resolved patch. Eligible connection failures are reported and use a bundled verified reference.
- Continue to choose standard or hexagonal and basic, strict or no architecture guardrails. Strict REST checks now cover MVC enabled implicitly by the sample.
- Generated verification runs sample integration tests, including real HTTP. Maven resolves the Mockito agent from the project’s dependencies.
- Project publication protects existing output directories and sibling ZIPs. POM text is XML-escaped. Cleanup is best-effort for owned output.
- The generator requires Java 25 and uses Spring Boot 4.2.0 internally.

Breaking CLI changes include minor-only Boot selectors (`4.1` and `4.2`), removal of Boot 3.x/4.0 generation, and a Java 25 default. Existing output paths must be cleared or changed before generating again.

See [compatibility](compatibility.md) and [release gates](maintainers/release-discipline.md).
