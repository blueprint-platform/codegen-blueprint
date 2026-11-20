# Migrate from Codegen Blueprint 1.x

Codegen Blueprint 2.0 generates new projects. Existing generated projects continue to build with their own sources and POMs. Spring Boot upgrades for those projects are a separate consumer migration.

| Before | With 2.0 |
| --- | --- |
| Boot selectors 3.4 and 3.5 | Choose `--boot 4.1` or `--boot 4.2`. Default is 4.2. |
| Default Java 21 output | Default is Java 25. Pass `--java 21` to retain Java 21 output. |
| Generator pins the output patch | Minor selectors resolve Central’s latest stable patch and report eligible connection fallback. |
| Generated Boot 3 dependencies and test wiring | Boot 4 starter/test modules, JUnit 6 ArchUnit integration and generated sample Failsafe tests. |

Update automation that passes Boot 3 selectors or assumes a fixed patch. Use a fresh target directory or artifact ID when comparing generated output. Run the generated wrapper with the target JDK and review the [compatibility reference](../compatibility.md).

To generate another Boot 3 project under the original contract, use the historical [v1.0.3 release](https://github.com/blueprint-platform/codegen-blueprint/releases/tag/v1.0.3).
