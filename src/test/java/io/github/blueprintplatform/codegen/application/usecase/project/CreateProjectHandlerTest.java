package io.github.blueprintplatform.codegen.application.usecase.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.blueprintplatform.codegen.application.error.exception.InvalidProjectRequestException;
import io.github.blueprintplatform.codegen.application.port.in.project.model.CreateProjectCommand;
import io.github.blueprintplatform.codegen.application.port.in.project.model.DependencyInput;
import io.github.blueprintplatform.codegen.application.port.in.project.model.summary.ProjectFileSummary;
import io.github.blueprintplatform.codegen.application.port.model.TargetResolution;
import io.github.blueprintplatform.codegen.application.port.model.TargetResolution.Source;
import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsPort;
import io.github.blueprintplatform.codegen.application.port.out.ProjectPublisherPort;
import io.github.blueprintplatform.codegen.application.port.out.output.ProjectOutputItem;
import io.github.blueprintplatform.codegen.application.usecase.project.mapper.CreateProjectResultMapper;
import io.github.blueprintplatform.codegen.application.usecase.project.mapper.ProjectBlueprintMapper;
import io.github.blueprintplatform.codegen.domain.error.exception.DomainViolationException;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.model.value.architecture.GuardrailsMode;
import io.github.blueprintplatform.codegen.domain.model.value.layout.ProjectLayout;
import io.github.blueprintplatform.codegen.domain.model.value.sample.SampleCodeOptions;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.JavaVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.PlatformTarget;
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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@Tag("unit")
@Tag("application")
class CreateProjectHandlerTest {

  @TempDir Path tempDir;

  @Test
  void resolvesTargetAfterProfileSelectionAndPublishesArtifacts() {
    var blueprintMapper = new ProjectBlueprintMapper();
    var resultMapper = new CreateProjectResultMapper();
    var fakeArtifacts = new FakeArtifactsPort();
    var requested = new SpringBootJvmTarget(JavaVersion.JAVA_21, SpringBootVersion.V4_2);
    var command = createProjectCommand("demo-app", requested, tempDir);
    var resolved = new SpringBootJvmTarget(JavaVersion.JAVA_21, SpringBootVersion.V4_2, "4.2.0");
    var resolution = new TargetResolution(resolved, Source.REMOTE_LATEST, null);
    List<String> calls = new ArrayList<>();

    var handler =
        new CreateProjectHandler(
            blueprintMapper,
            resultMapper,
            stack -> {
              assertThat(stack).isEqualTo(command.techStack());
              calls.add("profile");
              return blueprint -> {
                assertThat(blueprint.getPlatform().platformTarget()).isSameAs(resolved);
                calls.add("generate");
                return fakeArtifacts.generate(blueprint);
              };
            },
            target -> {
              assertThat(target).isSameAs(requested);
              calls.add("resolve");
              return resolution;
            },
            (target, artifact, resources) -> {
              calls.add("publish");
              assertThat(target).isEqualTo(tempDir);
              assertThat(artifact).isEqualTo("demo-app");
              var files = new ArrayList<ProjectOutputItem>();
              resources.forEach(
                  resource ->
                      files.add(new ProjectOutputItem(resource.relativePath(), false, false)));
              return new ProjectPublisherPort.PublishedProject(
                  target.resolve(artifact), target.resolve(artifact + ".zip"), files);
            });

    var result = handler.handle(command);
    assertThat(calls).containsExactly("profile", "resolve", "generate", "publish");

    assertThat(result.archivePath()).hasFileName("demo-app.zip");
    assertThat(result.projectRoot()).isEqualTo(tempDir.resolve("demo-app"));

    assertThat(result.targetResolution()).isSameAs(resolution);
    assertThat(result.project().platform().platformTarget()).isSameAs(resolved);

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

  @Test
  void rejectsInvalidRequestsBeforeSelectingProfileOrResolvingTarget() {
    var handler =
        new CreateProjectHandler(
            new ProjectBlueprintMapper(),
            new CreateProjectResultMapper(),
            stack -> {
              throw new AssertionError("Profile selection must follow validation");
            },
            target -> {
              throw new AssertionError("Invalid requests must not resolve a target");
            },
            (directory, artifact, resources) -> {
              throw new AssertionError("Unexpected publication");
            });

    assertThatThrownBy(() -> handler.handle(createProjectCommand("invalid/id", null, tempDir)))
        .isInstanceOf(DomainViolationException.class)
        .hasMessage("project.artifact-id.invalid.chars");
    assertThatThrownBy(() -> handler.handle(createProjectCommand("demo-app", null, tempDir)))
        .isInstanceOf(DomainViolationException.class)
        .hasMessage("platform.target.missing");
    var requested = new SpringBootJvmTarget(JavaVersion.JAVA_21, SpringBootVersion.V4_2);
    assertThatThrownBy(() -> handler.handle(createProjectCommand("demo-app", requested, null)))
        .isInstanceOf(InvalidProjectRequestException.class)
        .hasMessage("application.project.target-directory.required");
    var invalidScope = List.of(new DependencyInput("com.acme", "library", null, "Bogus"));
    assertThatThrownBy(
            () -> handler.handle(createProjectCommand("demo-app", requested, tempDir, invalidScope)))
        .isInstanceOfSatisfying(
            DomainViolationException.class,
            failure -> {
              assertThat(failure.getMessageKey()).isEqualTo("dependency.scope.unknown");
              assertThat(failure.getArgs()).containsExactly("bogus");
            });
  }

  private CreateProjectCommand createProjectCommand(
      String artifactId, PlatformTarget platformTarget, Path targetDirectory) {
    return createProjectCommand(artifactId, platformTarget, targetDirectory, List.of());
  }

  private CreateProjectCommand createProjectCommand(
      String artifactId, PlatformTarget platformTarget, Path targetDirectory,
      List<DependencyInput> dependencies) {
    var techStack = new TechStack(Framework.SPRING_BOOT, BuildTool.MAVEN, Language.JAVA);

    return new CreateProjectCommand(
        "com.acme",
        artifactId,
        "Demo App",
        "Demo project",
        "com.acme.demo",
        techStack,
        ProjectLayout.STANDARD,
        GuardrailsMode.NONE,
        platformTarget,
        dependencies,
        SampleCodeOptions.none(),
        targetDirectory);
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
