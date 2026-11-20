package io.github.blueprintplatform.codegen.application.usecase.project;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.blueprintplatform.codegen.application.port.in.project.model.CreateProjectCommand;
import io.github.blueprintplatform.codegen.application.port.in.project.model.summary.ProjectFileSummary;
import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsPort;
import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsSelector;
import io.github.blueprintplatform.codegen.application.port.out.ProjectPublisherPort;
import io.github.blueprintplatform.codegen.application.port.out.output.ProjectOutputItem;
import io.github.blueprintplatform.codegen.application.usecase.project.mapper.CreateProjectResultMapper;
import io.github.blueprintplatform.codegen.application.usecase.project.mapper.ProjectBlueprintMapper;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.model.value.architecture.GuardrailsMode;
import io.github.blueprintplatform.codegen.domain.model.value.layout.ProjectLayout;
import io.github.blueprintplatform.codegen.domain.model.value.sample.SampleCodeOptions;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.JavaVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootJvmTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.BuildTool;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Framework;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Language;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.TechStack;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedResource;
import io.github.blueprintplatform.codegen.domain.port.out.artifact.GeneratedTextResource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@Tag("unit")
@Tag("application")
class CreateProjectHandlerTest {

  @TempDir Path tempDir;

  @Test
  void resolvesPatchPublishesArtifactsAndReturnsSummary() {
    var blueprintMapper = new ProjectBlueprintMapper();
    var resultMapper = new CreateProjectResultMapper();
    var fakeArtifacts = new FakeArtifactsPort();
    var fakeSelector = new FakeSelector(fakeArtifacts);

    var handler =
        new CreateProjectHandler(
            blueprintMapper,
            resultMapper,
            fakeSelector,
            new BootVersionSelection(
                line -> "4.2.0",
                Map.of(SpringBootVersion.V4_1, "4.1.3", SpringBootVersion.V4_2, "4.2.0")),
            (target, artifact, resources) -> {
              assertThat(target).isEqualTo(tempDir);
              assertThat(artifact).isEqualTo("demo-app");
              var files = new ArrayList<ProjectOutputItem>();
              resources.forEach(
                  resource -> files.add(new ProjectOutputItem(resource.relativePath(), false, false)));
              return new ProjectPublisherPort.PublishedProject(
                  target.resolve(artifact), target.resolve(artifact + ".zip"), files);
            });

    var createProjectCommand = getCreateProjectCommand();

    var result = handler.handle(createProjectCommand);

    assertThat(result.archivePath()).hasFileName("demo-app.zip");
    assertThat(result.projectRoot()).isEqualTo(tempDir.resolve("demo-app"));

    assertThat(result.versionSource()).isEqualTo("CENTRAL_LATEST");
    assertThat(result.project().runtimeTarget().params()).containsEntry("springBootVersion", "4.2.0");

    assertThat(result.project().metadata().groupId()).isEqualTo("com.acme");
    assertThat(result.project().metadata().artifactId()).isEqualTo("demo-app");
    assertThat(result.project().metadata().projectName()).isEqualTo("Demo App");
    assertThat(result.project().metadata().projectDescription()).isEqualTo("Demo project");
    assertThat(result.project().metadata().packageName()).isEqualTo("com.acme.demo");
    assertThat(result.project().architecture().layout()).isEqualTo(ProjectLayout.STANDARD.key());
    assertThat(result.project().architecture().guardrailsMode())
        .isEqualTo(GuardrailsMode.NONE.key());
    assertThat(result.project().architecture().sampleCodeLevel())
        .isEqualTo(SampleCodeOptions.none().level().key());

    assertThat(result.project().files())
        .extracting(ProjectFileSummary::relativePath)
        .containsExactlyInAnyOrderElementsOf(fakeArtifacts.lastEmittedRelativePaths);

    assertThat(result.project().files()).allSatisfy(f -> assertThat(f.executable()).isFalse());
    assertThat(result.project().files()).allSatisfy(f -> assertThat(f.binary()).isFalse());
  }

  private CreateProjectCommand getCreateProjectCommand() {
    var techStack = new TechStack(Framework.SPRING_BOOT, BuildTool.MAVEN, Language.JAVA);
    var platformTarget =
        new SpringBootJvmTarget(JavaVersion.JAVA_21, SpringBootVersion.V4_2);

    return new CreateProjectCommand(
        "com.acme",
        "demo-app",
        "Demo App",
        "Demo project",
        "com.acme.demo",
        techStack,
        ProjectLayout.STANDARD,
        GuardrailsMode.NONE,
        platformTarget,
        List.of(),
        SampleCodeOptions.none(),
        tempDir);
  }

  static class FakeSelector implements ProjectArtifactsSelector {
    private final ProjectArtifactsPort delegate;

    FakeSelector(ProjectArtifactsPort delegate) {
      this.delegate = delegate;
    }

    @Override
    public ProjectArtifactsPort select(TechStack options) {
      return delegate;
    }
  }

  static class FakeArtifactsPort implements ProjectArtifactsPort {
    final List<Path> lastEmittedRelativePaths = new ArrayList<>();

    @Override
    public Iterable<GeneratedResource> generate(ProjectBlueprint bp) {
      List<GeneratedResource> files =
          List.of(
              new GeneratedTextResource(Path.of("pom.xml"), "<project/>", StandardCharsets.UTF_8),
              new GeneratedTextResource(Path.of(".gitignore"), "*.class", StandardCharsets.UTF_8),
              new GeneratedTextResource(
                  Path.of("src/main/java/com/acme/demo/DemoApplication.java"),
                  "class DemoApplication {}",
                  StandardCharsets.UTF_8),
              new GeneratedTextResource(
                  Path.of("src/test/java/com/acme/demo/DemoApplicationTests.java"),
                  "class DemoApplicationTests {}",
                  StandardCharsets.UTF_8),
              new GeneratedTextResource(
                  Path.of("src/main/resources/application.yml"),
                  "spring:\n  application:\n    name: demo-app",
                  StandardCharsets.UTF_8),
              new GeneratedTextResource(
                  Path.of("README.md"),
                  "# Demo App\n\nThis project was generated by Codegen Blueprint.",
                  StandardCharsets.UTF_8));

      lastEmittedRelativePaths.clear();
      for (var gf : files) {
        lastEmittedRelativePaths.add(gf.relativePath());
      }
      return files;
    }
  }
}
