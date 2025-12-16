package io.github.blueprintplatform.codegen.application.usecase.project.mapper;

import io.github.blueprintplatform.codegen.application.port.in.project.dto.CreateProjectResponse;
import io.github.blueprintplatform.codegen.application.port.in.project.dto.GeneratedFileSummary;
import io.github.blueprintplatform.codegen.application.port.in.project.dto.ProjectDependencySummary;
import io.github.blueprintplatform.codegen.application.port.in.project.dto.ProjectGenerationSummary;
import io.github.blueprintplatform.codegen.application.port.out.output.ProjectOutputItem;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.model.value.architecture.ArchitectureSpec;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependencies;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependency;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.DependencyCoordinates;
import io.github.blueprintplatform.codegen.domain.model.value.metadata.ProjectMetadata;
import io.github.blueprintplatform.codegen.domain.model.value.tech.PlatformSpec;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class CreateProjectResponseMapper {

  public CreateProjectResponse from(
      ProjectBlueprint blueprint,
      Path projectRoot,
      List<ProjectOutputItem> projectOutputItems,
      Path archivePath) {
    ProjectGenerationSummary projectSummary = toProjectGenerationSummary(blueprint);
    List<GeneratedFileSummary> files =
        projectOutputItems.stream().map(this::toGeneratedFileSummary).toList();
    return new CreateProjectResponse(projectSummary, projectRoot, archivePath, files);
  }

  private GeneratedFileSummary toGeneratedFileSummary(ProjectOutputItem item) {
    return new GeneratedFileSummary(item.relativePath(), item.binary(), item.executable());
  }

  private ProjectGenerationSummary toProjectGenerationSummary(ProjectBlueprint blueprint) {
    ProjectMetadata metadata = blueprint.getMetadata();
    PlatformSpec platform = blueprint.getPlatform();
    ArchitectureSpec architecture = blueprint.getArchitecture();
    Dependencies dependencies = blueprint.getDependencies();

    List<ProjectDependencySummary> dependencySummaries =
        dependencies.asList().stream().map(this::toProjectDependencySummary).toList();

    return new ProjectGenerationSummary(
        metadata.identity().groupId().value(),
        metadata.identity().artifactId().value(),
        metadata.name().value(),
        metadata.description().value(),
        metadata.packageName().value(),
        platform.techStack(),
        architecture.layout(),
        architecture.governance().mode(),
        platform.platformTarget(),
        architecture.sampleCodeOptions(),
        dependencySummaries);
  }

  private ProjectDependencySummary toProjectDependencySummary(Dependency dependency) {
    DependencyCoordinates coordinates = dependency.coordinates();
    String version = dependency.version() != null ? dependency.version().value() : null;
    String scope =
        dependency.scope() != null ? dependency.scope().name().toLowerCase(Locale.ROOT) : null;
    return new ProjectDependencySummary(
        coordinates.groupId().value(), coordinates.artifactId().value(), version, scope);
  }
}
