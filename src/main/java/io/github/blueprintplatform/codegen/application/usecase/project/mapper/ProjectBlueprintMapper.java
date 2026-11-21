package io.github.blueprintplatform.codegen.application.usecase.project.mapper;

import io.github.blueprintplatform.codegen.application.error.exception.InvalidProjectRequestException;
import io.github.blueprintplatform.codegen.application.port.in.project.model.CreateProjectCommand;
import io.github.blueprintplatform.codegen.application.port.in.project.model.DependencyInput;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.model.value.architecture.ArchitectureGovernance;
import io.github.blueprintplatform.codegen.domain.model.value.architecture.ArchitectureSpec;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependencies;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependency;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.DependencyCoordinates;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.DependencyScope;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.DependencyVersion;
import io.github.blueprintplatform.codegen.domain.model.value.identity.ArtifactId;
import io.github.blueprintplatform.codegen.domain.model.value.identity.GroupId;
import io.github.blueprintplatform.codegen.domain.model.value.identity.ProjectIdentity;
import io.github.blueprintplatform.codegen.domain.model.value.metadata.ProjectMetadata;
import io.github.blueprintplatform.codegen.domain.model.value.naming.ProjectDescription;
import io.github.blueprintplatform.codegen.domain.model.value.naming.ProjectName;
import io.github.blueprintplatform.codegen.domain.model.value.pkg.PackageName;
import io.github.blueprintplatform.codegen.domain.model.value.tech.PlatformSpec;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.PlatformTarget;
import io.github.blueprintplatform.codegen.domain.policy.tech.CompatibilityPolicy;
import java.util.ArrayList;
import java.util.List;

public class ProjectBlueprintMapper {

  public ValidatedProject validate(CreateProjectCommand c) {
    ProjectMetadata metadata =
        new ProjectMetadata(
            new ProjectIdentity(new GroupId(c.groupId()), new ArtifactId(c.artifactId())),
            new ProjectName(c.projectName()),
            new ProjectDescription(c.projectDescription()),
            new PackageName(c.packageName()));

    PlatformSpec platform = new PlatformSpec(c.techStack(), c.platformTarget());

    Dependencies dependencies = mapDependencies(c.dependencies());

    CompatibilityPolicy.ensureCompatible(platform);
    if (c.layout() == null) {
      throw new InvalidProjectRequestException("application.project.layout.required");
    }
    if (c.sampleCodeOptions() == null) {
      throw new InvalidProjectRequestException("application.project.sample-code.required");
    }
    var architecture =
        new ArchitectureSpec(
            c.layout(), new ArchitectureGovernance(c.guardrailsMode()), c.sampleCodeOptions());
    return new ValidatedProject(metadata, platform, architecture, dependencies);
  }

  public ProjectBlueprint from(ValidatedProject project, PlatformTarget resolvedTarget) {
    var platform = new PlatformSpec(project.platform().techStack(), resolvedTarget);
    return ProjectBlueprint.of(
        project.metadata(), platform, project.architecture(), project.dependencies());
  }

  public record ValidatedProject(
      ProjectMetadata metadata,
      PlatformSpec platform,
      ArchitectureSpec architecture,
      Dependencies dependencies) {}

  private Dependencies mapDependencies(List<DependencyInput> raw) {
    if (raw == null || raw.isEmpty()) {
      return Dependencies.of(List.of());
    }

    List<Dependency> items = new ArrayList<>(raw.size());
    for (DependencyInput d : raw) {
      DependencyVersion version =
          (d.version() == null || d.version().isBlank())
              ? null
              : new DependencyVersion(d.version());

      DependencyScope scope = DependencyScope.fromOptionalKey(d.scope());

      items.add(
          new Dependency(
              new DependencyCoordinates(new GroupId(d.groupId()), new ArtifactId(d.artifactId())),
              version,
              scope));
    }

    return Dependencies.of(items);
  }
}
