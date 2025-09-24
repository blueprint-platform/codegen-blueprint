package io.github.bsayli.codegen.initializr.application.usecase.createproject;

import io.github.bsayli.codegen.initializr.domain.factory.ProjectBlueprintFactory;
import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import io.github.bsayli.codegen.initializr.domain.model.value.dependency.Dependencies;
import io.github.bsayli.codegen.initializr.domain.model.value.dependency.Dependency;
import io.github.bsayli.codegen.initializr.domain.model.value.dependency.DependencyCoordinates;
import io.github.bsayli.codegen.initializr.domain.model.value.dependency.DependencyScope;
import io.github.bsayli.codegen.initializr.domain.model.value.dependency.DependencyVersion;
import io.github.bsayli.codegen.initializr.domain.model.value.identity.ArtifactId;
import io.github.bsayli.codegen.initializr.domain.model.value.identity.GroupId;
import java.util.List;

public class ProjectBlueprintMapper {

  public ProjectBlueprint toDomain(CreateProjectCommand c) {
    List<Dependency> deps =
        c.dependencies().stream()
            .map(
                d ->
                    new Dependency(
                        new DependencyCoordinates(
                            new GroupId(d.groupId()), new ArtifactId(d.artifactId())),
                        (d.version() == null || d.version().isBlank())
                            ? null
                            : new DependencyVersion(d.version()),
                        (d.scope() == null || d.scope().isBlank())
                            ? null
                            : DependencyScope.valueOf(d.scope().trim().toUpperCase())))
            .toList();

    var depsWrapper = Dependencies.of(deps);

    if (c.preferredJava() != null && c.preferredBoot() != null) {
      return ProjectBlueprintFactory.fromPrimitives(
          c.groupId(),
          c.artifactId(),
          c.projectName(),
          c.projectDescription(),
          c.packageName(),
          c.buildOptions(),
          c.preferredJava(),
          c.preferredBoot(),
          depsWrapper);
    }
    return ProjectBlueprintFactory.fromPrimitivesWithAutoTarget(
        c.groupId(),
        c.artifactId(),
        c.projectName(),
        c.projectDescription(),
        c.packageName(),
        c.buildOptions(),
        depsWrapper);
  }
}
