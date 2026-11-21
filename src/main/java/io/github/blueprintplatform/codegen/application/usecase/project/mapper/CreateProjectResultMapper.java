package io.github.blueprintplatform.codegen.application.usecase.project.mapper;

import io.github.blueprintplatform.codegen.application.port.in.project.model.CreateProjectResult;
import io.github.blueprintplatform.codegen.application.port.in.project.model.ProjectSummary;
import io.github.blueprintplatform.codegen.application.port.in.project.model.summary.ArchitectureSpecSummary;
import io.github.blueprintplatform.codegen.application.port.in.project.model.summary.ProjectDependencySummary;
import io.github.blueprintplatform.codegen.application.port.in.project.model.summary.ProjectFileSummary;
import io.github.blueprintplatform.codegen.application.port.in.project.model.summary.ProjectMetadataSummary;
import io.github.blueprintplatform.codegen.application.port.model.TargetResolution;
import io.github.blueprintplatform.codegen.application.port.out.ProjectPublisherPort.PublishedProject;
import io.github.blueprintplatform.codegen.application.port.out.output.ProjectOutputItem;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.model.value.architecture.ArchitectureSpec;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependencies;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependency;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.DependencyCoordinates;
import io.github.blueprintplatform.codegen.domain.model.value.metadata.ProjectMetadata;
import java.util.List;
import java.util.Locale;

public class CreateProjectResultMapper {

  public CreateProjectResult from(
      ProjectBlueprint blueprint, PublishedProject published, TargetResolution resolution) {

    ProjectSummary project = toProjectSummary(blueprint, published.files());
    return new CreateProjectResult(project, published.root(), published.archive(), resolution);
  }

  private ProjectSummary toProjectSummary(
      ProjectBlueprint blueprint, List<ProjectOutputItem> projectOutputItems) {
    ProjectMetadata metadata = blueprint.getMetadata();
    ArchitectureSpec architecture = blueprint.getArchitecture();
    Dependencies dependencies = blueprint.getDependencies();

    ProjectMetadataSummary metadataSummary = toMetadataSummary(metadata);
    ArchitectureSpecSummary architectureSummary = toArchitectureSummary(architecture);

    List<ProjectDependencySummary> dependencySummaries =
        dependencies.asList().stream().map(this::toProjectDependencySummary).toList();

    List<ProjectFileSummary> files =
        projectOutputItems.stream().map(this::toProjectFileSummary).toList();

    return new ProjectSummary(
        metadataSummary,
        blueprint.getPlatform(),
        architectureSummary,
        dependencySummaries,
        files);
  }

  private ProjectMetadataSummary toMetadataSummary(ProjectMetadata metadata) {
    return new ProjectMetadataSummary(
        metadata.identity().groupId().value(),
        metadata.identity().artifactId().value(),
        metadata.name().value(),
        metadata.description().value(),
        metadata.packageName().value());
  }

  private ArchitectureSpecSummary toArchitectureSummary(ArchitectureSpec architecture) {
    String layout = architecture.layout().key();
    String guardrailsMode = architecture.governance().mode().key();
    String sampleCode = architecture.sampleCodeOptions().level().key();

    return new ArchitectureSpecSummary(layout, guardrailsMode, sampleCode);
  }

  private ProjectFileSummary toProjectFileSummary(ProjectOutputItem item) {
    return new ProjectFileSummary(item.relativePath(), item.binary(), item.executable());
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
