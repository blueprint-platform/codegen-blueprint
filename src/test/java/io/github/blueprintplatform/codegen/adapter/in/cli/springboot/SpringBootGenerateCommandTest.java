package io.github.blueprintplatform.codegen.adapter.in.cli.springboot;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.blueprintplatform.codegen.adapter.in.cli.mapper.CreateProjectCommandMapper;
import io.github.blueprintplatform.codegen.adapter.in.cli.request.CliProjectRequest;
import io.github.blueprintplatform.codegen.adapter.in.cli.request.model.CliRuntimeTargetKeys;
import io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option.SpringBootArchitectureGuardrailsOption;
import io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option.SpringBootBuildToolOption;
import io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option.SpringBootDependencyOption;
import io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option.SpringBootJavaVersionOption;
import io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option.SpringBootLanguageOption;
import io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option.SpringBootLayoutOption;
import io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option.SpringBootSampleCodeOption;
import io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option.SpringBootVersionOption;
import io.github.blueprintplatform.codegen.application.port.in.project.CreateProjectPort;
import io.github.blueprintplatform.codegen.application.port.in.project.model.CreateProjectCommand;
import io.github.blueprintplatform.codegen.application.port.in.project.model.CreateProjectResult;
import io.github.blueprintplatform.codegen.application.port.in.project.model.ProjectSummary;
import io.github.blueprintplatform.codegen.application.port.in.project.model.summary.ArchitectureSpecSummary;
import io.github.blueprintplatform.codegen.application.port.in.project.model.summary.ProjectMetadataSummary;
import io.github.blueprintplatform.codegen.application.port.model.TargetResolution;
import io.github.blueprintplatform.codegen.application.port.model.TargetResolution.Source;
import io.github.blueprintplatform.codegen.domain.model.value.tech.PlatformSpec;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.JavaVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootJvmTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.BuildTool;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Framework;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Language;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.TechStack;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.support.ResourceBundleMessageSource;

@Tag("unit")
@Tag("adapter")
@ExtendWith(OutputCaptureExtension.class)
class SpringBootGenerateCommandTest {

  @ParameterizedTest
  @CsvSource({
    "REMOTE_LATEST, CENTRAL_LATEST,",
    "BUNDLED_FALLBACK, BUNDLED_FALLBACK, metadata: ConnectException"
  })
  @DisplayName("call() should build CliProjectRequest and invoke use case with mapped command")
  void call_shouldBuildRequestAndInvokeUseCase(
      Source source, String label, String warning, CapturedOutput output) {
    var mapper = new RecordingMapper();
    var useCase = new StubCreateProjectPort(source);

    var messages = new ResourceBundleMessageSource();
    messages.setBasename("messages");
    var cmd = new SpringBootGenerateCommand(mapper, useCase, messages);

    cmd.groupId = "com.acme";
    cmd.artifactId = "demo-app";
    cmd.name = "Demo App";
    cmd.description = "Demo application for Acme";
    cmd.packageName = "com.acme.demo";

    cmd.buildTool = SpringBootBuildToolOption.MAVEN;
    cmd.language = SpringBootLanguageOption.JAVA;
    cmd.javaVersion = SpringBootJavaVersionOption.JAVA_21;
    cmd.bootVersion = SpringBootVersionOption.V4_2;

    cmd.layout = SpringBootLayoutOption.STANDARD;
    cmd.sampleCode = SpringBootSampleCodeOption.NONE;
    cmd.guardrails = SpringBootArchitectureGuardrailsOption.NONE;

    cmd.dependencies = List.of(SpringBootDependencyOption.WEB);

    Path expectedTargetDir = Path.of(".");
    cmd.targetDirectory = expectedTargetDir;

    Integer exitCode = cmd.call();

    assertThat(exitCode).isZero();
    assertThat(output.getAll()).contains("bootSelector=4.2 bootVersion=4.2.0 source=" + label);
    if (warning != null) {
      assertThat(output.getAll()).contains(label + ": " + warning);
    }

    assertThat(mapper.lastRequest).isNotNull();

    CliProjectRequest r = mapper.lastRequest;

    assertThat(r.metadata().groupId()).isEqualTo("com.acme");
    assertThat(r.metadata().artifactId()).isEqualTo("demo-app");
    assertThat(r.metadata().projectName()).isEqualTo("Demo App");
    assertThat(r.metadata().projectDescription()).isEqualTo("Demo application for Acme");
    assertThat(r.metadata().packageName()).isEqualTo("com.acme.demo");

    assertThat(r.targetDirectory()).isEqualTo(expectedTargetDir);

    assertThat(r.techStack().framework()).isEqualTo("spring-boot");
    assertThat(r.techStack().buildTool()).isEqualTo("maven");
    assertThat(r.techStack().language()).isEqualTo("java");

    assertThat(r.runtimeTarget().type()).isEqualTo(CliRuntimeTargetKeys.TYPE_SPRING_BOOT_JVM);
    assertThat(r.runtimeTarget().params())
        .containsEntry(CliRuntimeTargetKeys.PARAM_JAVA_VERSION, "21")
        .containsEntry(CliRuntimeTargetKeys.PARAM_SPRING_BOOT_VERSION, "4.2");

    assertThat(r.architecture().layout()).isEqualTo("standard");
    assertThat(r.architecture().guardrailsMode()).isEqualTo("none");
    assertThat(r.architecture().sampleCodeLevel()).isEqualTo("none");

    assertThat(r.dependencies()).hasSize(1);
    assertThat(r.dependencies().getFirst().groupId()).isEqualTo("org.springframework.boot");
    assertThat(r.dependencies().getFirst().artifactId()).isEqualTo("spring-boot-starter-webmvc");
    assertThat(r.dependencies().getFirst().version()).isNull();
    assertThat(r.dependencies().getFirst().scope()).isNull();

    assertThat(useCase.lastCommand).isSameAs(mapper.returnedCommand);
  }

  static class RecordingMapper extends CreateProjectCommandMapper {

    final CreateProjectCommand returnedCommand = null;
    CliProjectRequest lastRequest;

    @Override
    public CreateProjectCommand from(CliProjectRequest request) {
      this.lastRequest = request;
      return returnedCommand;
    }
  }

  static class StubCreateProjectPort implements CreateProjectPort {
    private final Source source;
    private CreateProjectCommand lastCommand;

    StubCreateProjectPort(Source source) {
      this.source = source;
    }

    private TargetResolution.Warning fallbackWarning() {
      return source == Source.BUNDLED_FALLBACK
          ? new TargetResolution.Warning(
              "adapter.springboot.repository.unavailable", List.of("metadata", "ConnectException"))
          : null;
    }

    @Override
    public CreateProjectResult handle(CreateProjectCommand command) {
      this.lastCommand = command;
      var metadata =
          new ProjectMetadataSummary(
              "com.acme", "demo-app", "Demo App", "Demo application for Acme", "com.acme.demo");

      var target = new SpringBootJvmTarget(JavaVersion.JAVA_21, SpringBootVersion.V4_2, "4.2.0");
      var platform =
          new PlatformSpec(
              new TechStack(Framework.SPRING_BOOT, BuildTool.MAVEN, Language.JAVA), target);

      var architecture = new ArchitectureSpecSummary("standard", "none", "none");

      var project =
          new ProjectSummary(
              metadata, platform, architecture, List.of(), List.of());

      return new CreateProjectResult(
          project,
          Path.of("."),
          Path.of("demo-app.zip"),
          new TargetResolution(target, source, fallbackWarning()));
    }
  }
}
