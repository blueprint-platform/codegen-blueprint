package io.github.blueprintplatform.codegen.adapter.in.cli.mapper;

import io.github.blueprintplatform.codegen.adapter.error.exception.UnsupportedRuntimeTargetTypeException;
import io.github.blueprintplatform.codegen.adapter.in.cli.request.CliProjectRequest;
import io.github.blueprintplatform.codegen.adapter.in.cli.request.model.CliDependency;
import io.github.blueprintplatform.codegen.adapter.in.cli.request.model.CliRuntimeTarget;
import io.github.blueprintplatform.codegen.adapter.in.cli.request.model.CliRuntimeTargetKeys;
import io.github.blueprintplatform.codegen.adapter.in.cli.request.model.CliTechStack;
import io.github.blueprintplatform.codegen.application.port.in.project.dto.request.CreateProjectCommand;
import io.github.blueprintplatform.codegen.application.port.in.project.dto.request.DependencyInput;
import io.github.blueprintplatform.codegen.domain.model.value.architecture.EnforcementMode;
import io.github.blueprintplatform.codegen.domain.model.value.layout.ProjectLayout;
import io.github.blueprintplatform.codegen.domain.model.value.sample.SampleCodeLevel;
import io.github.blueprintplatform.codegen.domain.model.value.sample.SampleCodeOptions;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.JavaVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.PlatformTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootJvmTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.BuildTool;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Framework;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.Language;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.TechStack;
import java.util.List;
import java.util.Map;

public class CreateProjectCommandMapper {

  public CreateProjectCommand from(CliProjectRequest request) {
    var metadata = request.metadata();
    var techStack = toTechStack(request.techStack());
    var platformTarget = toPlatformTarget(request.runtimeTarget());

    ProjectLayout layout = ProjectLayout.fromKey(request.architecture().layout());
    EnforcementMode enforcementMode =
        EnforcementMode.fromKey(request.architecture().enforcementMode());

    SampleCodeLevel sampleCodeLevel =
        SampleCodeLevel.fromKey(request.architecture().sampleCodeLevel());
    SampleCodeOptions sampleCodeOptions = new SampleCodeOptions(sampleCodeLevel);

    List<DependencyInput> dependencies = toDependencyInputs(request.dependencies());

    return new CreateProjectCommand(
        metadata.groupId(),
        metadata.artifactId(),
        metadata.projectName(),
        metadata.projectDescription(),
        metadata.packageName(),
        techStack,
        layout,
        enforcementMode,
        platformTarget,
        dependencies,
        sampleCodeOptions,
        request.targetDirectory());
  }

  private TechStack toTechStack(CliTechStack cli) {
    Framework framework = Framework.fromKey(cli.framework());
    BuildTool buildTool = BuildTool.fromKey(cli.buildTool());
    Language language = Language.fromKey(cli.language());
    return new TechStack(framework, buildTool, language);
  }

  private PlatformTarget toPlatformTarget(CliRuntimeTarget cli) {
    String type = cli.type();
    Map<String, String> params = cli.params();

    if (CliRuntimeTargetKeys.TYPE_SPRING_BOOT_JVM.equals(type)) {
      String rawJava = params.get(CliRuntimeTargetKeys.PARAM_JAVA_VERSION);
      String rawBoot = params.get(CliRuntimeTargetKeys.PARAM_SPRING_BOOT_VERSION);

      JavaVersion javaVersion = JavaVersion.fromKey(rawJava);
      SpringBootVersion bootVersion = SpringBootVersion.fromKey(rawBoot);

      return new SpringBootJvmTarget(javaVersion, bootVersion);
    }

    throw new UnsupportedRuntimeTargetTypeException(cli.type());
  }

  private List<DependencyInput> toDependencyInputs(List<CliDependency> cliDependencies) {
    if (cliDependencies == null || cliDependencies.isEmpty()) {
      return List.of();
    }
    return cliDependencies.stream()
        .map(d -> new DependencyInput(d.groupId(), d.artifactId(), d.version(), d.scope()))
        .toList();
  }
}
