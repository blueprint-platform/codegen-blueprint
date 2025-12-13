package io.github.blueprintplatform.codegen.adapter.in.cli.springboot;

import io.github.blueprintplatform.codegen.adapter.in.cli.CliProjectRequest;
import io.github.blueprintplatform.codegen.adapter.in.cli.springboot.dependency.SpringBootDependencyAlias;
import io.github.blueprintplatform.codegen.application.usecase.project.model.CreateProjectCommand;
import io.github.blueprintplatform.codegen.application.usecase.project.model.DependencyInput;
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
import java.util.ArrayList;
import java.util.List;

public class CreateProjectCommandMapper {

  public CreateProjectCommand from(
      CliProjectRequest request,
      BuildTool buildTool,
      Language language,
      JavaVersion javaVersion,
      SpringBootVersion bootVersion) {

    TechStack techStack = new TechStack(Framework.SPRING_BOOT, buildTool, language);
    PlatformTarget platformTarget = new SpringBootJvmTarget(javaVersion, bootVersion);
    ProjectLayout layout = ProjectLayout.fromKey(request.layoutKey());
    SampleCodeLevel sampleCodeLevel = SampleCodeLevel.fromKey(request.sampleCodeLevelKey());
    List<DependencyInput> dependencies = toDependencyInputs(request.dependencies());
    SampleCodeOptions sampleCodeOptions = new SampleCodeOptions(sampleCodeLevel);

    return new CreateProjectCommand(
        request.groupId(),
        request.artifactId(),
        request.name(),
        request.description(),
        request.packageName(),
        techStack,
        layout,
        platformTarget,
        dependencies,
        sampleCodeOptions,
        request.targetDirectory());
  }

  private List<DependencyInput> toDependencyInputs(List<String> aliases) {
    if (aliases == null || aliases.isEmpty()) {
      return List.of();
    }

    List<DependencyInput> result = new ArrayList<>();

    for (String raw : aliases) {
      if (raw == null || raw.isBlank()) {
        continue;
      }

      SpringBootDependencyAlias alias = SpringBootDependencyAlias.fromKey(raw);

      result.add(new DependencyInput(alias.groupId(), alias.artifactId(), null, null));
    }

    return List.copyOf(result);
  }
}
