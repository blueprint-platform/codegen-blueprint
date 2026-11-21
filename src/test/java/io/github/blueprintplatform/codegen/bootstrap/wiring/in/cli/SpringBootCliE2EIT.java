package io.github.blueprintplatform.codegen.bootstrap.wiring.in.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.blueprintplatform.codegen.application.port.model.TargetResolution;
import io.github.blueprintplatform.codegen.application.port.out.ProjectTargetResolver;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootJvmTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@Tag("integration")
@Tag("cli-e2e")
class SpringBootCliE2EIT {

  @TempDir Path tempDir;
  @Autowired private CodegenCliExecutor cliExecutor;

  @MockitoBean(name = "springBootTargetResolver", enforceOverride = true)
  private ProjectTargetResolver targetResolver;

  @BeforeEach
  void resolveFixedVersions() {
    var versions =
        Map.of(
            SpringBootVersion.V4_1, "4.1.3",
            SpringBootVersion.V4_2, "4.2.0");
    when(targetResolver.resolve(any()))
        .thenAnswer(
            invocation -> {
              SpringBootJvmTarget target = invocation.getArgument(0);
              return new TargetResolution(
                  new SpringBootJvmTarget(
                      target.java(), target.springBoot(), versions.get(target.springBoot())),
                  TargetResolution.Source.REMOTE_LATEST,
                  null);
            });
  }

  static Stream<Arguments> scenarios() {
    return Stream.of(
        Arguments.of(true, "21", "4.2", "4.2.0", null),
        Arguments.of(true, "25", "4.2", "4.2.0", null),
        Arguments.of(true, "21", "4.1", "4.1.3", null),
        Arguments.of(true, "25", "4.1", "4.1.3", null),
        Arguments.of(false, "25", "4.1.3", null, "4.1.3"));
  }

  private static String artifactId(String java, String boot) {
    String bootKey = boot.replace(".", "");
    return "greeting-j" + java + "-b" + bootKey;
  }

  private static List<String> zipEntries(ZipFile zipFile) {
    Enumeration<? extends ZipEntry> entries = zipFile.entries();
    List<String> names = new ArrayList<>();
    while (entries.hasMoreElements()) {
      names.add(entries.nextElement().getName());
    }
    return names;
  }

  private static String readTextEntry(ZipFile zipFile, ZipEntry entry) throws IOException {
    try (InputStream in = zipFile.getInputStream(entry)) {
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  private static CapturedIo captureIo(CodegenCliExecutor executor, String[] args) {
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    try (PrintStream errPs = new PrintStream(err, true, StandardCharsets.UTF_8);
        PrintStream outPs = new PrintStream(out, true, StandardCharsets.UTF_8)) {

      int exitCode = executor.execute(args, outPs, errPs);
      return new CapturedIo(
          exitCode, out.toString(StandardCharsets.UTF_8), err.toString(StandardCharsets.UTF_8));
    }
  }

  private static void assertSpringBootParentVersion(String pomXml, String expectedVersion) {
    assertThat(pomXml)
        .contains("<parent>")
        .contains("<groupId>org.springframework.boot</groupId>")
        .contains("<artifactId>spring-boot-starter-parent</artifactId>")
        .contains("<version>" + expectedVersion + "</version>");
  }

  @ParameterizedTest(name = "java={1}, boot={2} shouldSucceed={0}")
  @MethodSource("scenarios")
  @DisplayName(
      "CLI springboot should generate valid archives for supported targets and fail for"
          + " incompatible target")
  void cli_springboot_matrix_shouldBehaveAsExpected(
      boolean shouldSucceed,
      String java,
      String boot,
      String expectedBootPatch,
      String expectedErrorCode)
      throws IOException {

    String artifactId = artifactId(java, boot);
    String packageName = "io.github.blueprintplatform." + artifactId.replace("-", "");

    String[] args = {
      "springboot",
      "--group-id",
      "io.github.blueprintplatform",
      "--artifact-id",
      artifactId,
      "--name",
      "Greeting",
      "--description",
      "Greeting sample built with hexagonal architecture",
      "--package-name",
      packageName,
      "--layout",
      "hexagonal",
      "--guardrails",
      "basic",
      "--sample-code",
      "basic",
      "--java",
      java,
      "--boot",
      boot,
      "--dependency",
      "web",
      "--dependency",
      "data_jpa",
      "--dependency",
      "actuator",
      "--target-dir",
      tempDir.toString()
    };

    CapturedIo io = captureIo(cliExecutor, args);

    Path archivePath = tempDir.resolve(artifactId + ".zip");

    if (shouldSucceed) {
      assertThat(io.exitCode()).isZero();

      assertThat(Files.exists(archivePath))
          .as("Archive should be created at target-dir/%s.zip".formatted(artifactId))
          .isTrue();

      try (ZipFile zipFile = new ZipFile(archivePath.toFile())) {
        List<String> entryNames = zipEntries(zipFile);

        assertThat(entryNames).contains(artifactId + "/");

        assertThat(entryNames).contains(artifactId + "/pom.xml");
        assertThat(entryNames).contains(artifactId + "/src/main/resources/application.yml");
        assertThat(entryNames).contains(artifactId + "/README.md");

        assertThat(entryNames)
            .anySatisfy(
                name ->
                    assertThat(name)
                        .startsWith(
                            artifactId + "/src/main/java/" + packageName.replace('.', '/') + "/")
                        .endsWith("Application.java"));

        assertThat(entryNames)
            .anySatisfy(
                name ->
                    assertThat(name)
                        .startsWith(
                            artifactId + "/src/test/java/" + packageName.replace('.', '/') + "/")
                        .endsWith("ApplicationTests.java"));

        ZipEntry pomEntry = zipFile.getEntry(artifactId + "/pom.xml");
        assertThat(pomEntry).isNotNull();
        String pomXml = readTextEntry(zipFile, pomEntry);

        assertThat(pomXml)
            .contains("<groupId>io.github.blueprintplatform</groupId>")
            .contains("<artifactId>" + artifactId + "</artifactId>")
            .contains("<java.version>" + java + "</java.version>")
            .contains("spring-boot-starter-webmvc")
            .contains("spring-boot-starter-data-jpa")
            .contains("spring-boot-starter-actuator")
            .contains("spring-boot-starter-test");

        assertSpringBootParentVersion(pomXml, expectedBootPatch);

        ZipEntry appYamlEntry =
            zipFile.getEntry(artifactId + "/src/main/resources/application.yml");
        assertThat(appYamlEntry).isNotNull();
        String appYaml = readTextEntry(zipFile, appYamlEntry);

        assertThat(appYaml).contains("spring:").contains("application:").contains("name:");

        ZipEntry readmeEntry = zipFile.getEntry(artifactId + "/README.md");
        assertThat(readmeEntry).isNotNull();
        String readme = readTextEntry(zipFile, readmeEntry);

        assertThat(readme).contains("Greeting").contains(packageName);
      }
    } else {
      assertThat(io.exitCode()).isNotZero();
      assertThat(Files.exists(archivePath))
          .as("Archive should NOT be created for incompatible target")
          .isFalse();

      assertThat(io.stderr()).contains(expectedErrorCode);
    }
  }

  @Test
  void reportsInvalidMavenMetadataBeforeResolvingOrPublishing() {
    var io = generate("Greeting with ${project.version}");

    verifyNoInteractions(targetResolver);

    assertThat(io.exitCode()).isEqualTo(3);
    assertThat(io.stderr())
        .contains("Project description must be literal Maven XML without property expressions.")
        .contains("(code: adapter.pom.metadata.invalid)");
    assertThat(tempDir.resolve("greeting")).doesNotExist();
    assertThat(tempDir.resolve("greeting.zip")).doesNotExist();
  }

  @Test
  void reportsOutputCollisionAndPreservesTheExistingArchive() throws IOException {
    var archive = Files.writeString(tempDir.resolve("greeting.zip"), "user content");
    var io = generate("Greeting example");

    assertThat(io.exitCode()).isEqualTo(3);
    assertThat(io.stderr())
        .contains("Could not write project output")
        .contains("(code: adapter.project.write.failed)");
    assertThat(archive).hasContent("user content");
    assertThat(tempDir.resolve("greeting")).doesNotExist();
  }

  @Test
  void rejectsRichSampleWithoutResolvingOrPublishing() {
    var io = generate("Greeting example", "--sample-code", "rich");

    verifyNoInteractions(targetResolver);
    assertThat(io.exitCode()).isEqualTo(2);
    assertThat(io.stderr()).contains("adapter.cli.springboot.sample-code.unknown");
    assertThat(tempDir.resolve("greeting.zip")).doesNotExist();
  }

  @Test
  void rejectsScopedDependencySyntaxOnTheAliasOnlyCli() {
    var io = generate("Greeting example", "--dependency", "web:test");

    verifyNoInteractions(targetResolver);
    assertThat(io.exitCode()).isEqualTo(2);
    assertThat(io.stderr()).contains("adapter.cli.springboot.dependency.unknown");
    assertThat(tempDir.resolve("greeting.zip")).doesNotExist();
  }

  private CapturedIo generate(String description, String... options) {
    var args =
        new ArrayList<>(List.of(
          "springboot",
          "--group-id",
          "com.example",
          "--artifact-id",
          "greeting",
          "--name",
          "Greeting",
          "--description",
          description,
          "--package-name",
          "com.example.greeting",
          "--target-dir",
          tempDir.toString()));
    args.addAll(Arrays.asList(options));
    return captureIo(cliExecutor, args.toArray(String[]::new));
  }

  private record CapturedIo(int exitCode, String stdout, String stderr) {}
}
