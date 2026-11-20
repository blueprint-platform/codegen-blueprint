package io.github.blueprintplatform.codegen.application.usecase.project;

import io.github.blueprintplatform.codegen.application.port.in.project.CreateProjectPort;
import io.github.blueprintplatform.codegen.application.port.in.project.model.CreateProjectCommand;
import io.github.blueprintplatform.codegen.application.port.in.project.model.CreateProjectResult;
import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsPort;
import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsSelector;
import io.github.blueprintplatform.codegen.application.port.out.ProjectPublisherPort;
import io.github.blueprintplatform.codegen.application.usecase.project.mapper.CreateProjectResultMapper;
import io.github.blueprintplatform.codegen.application.usecase.project.mapper.ProjectBlueprintMapper;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootJvmTarget;

public class CreateProjectHandler implements CreateProjectPort {

  private final ProjectBlueprintMapper blueprintMapper;
  private final CreateProjectResultMapper responseMapper;
  private final ProjectArtifactsSelector artifactsSelector;
  private final ProjectPublisherPort publisher;
  private final BootVersionSelection bootVersions;

  public CreateProjectHandler(
      ProjectBlueprintMapper blueprintMapper,
      CreateProjectResultMapper responseMapper,
      ProjectArtifactsSelector artifactsSelector,
      BootVersionSelection bootVersions,
      ProjectPublisherPort publisher) {
    this.blueprintMapper = blueprintMapper;
    this.responseMapper = responseMapper;
    this.artifactsSelector = artifactsSelector;
    this.bootVersions = bootVersions;
    this.publisher = publisher;
  }

  @Override
  public CreateProjectResult handle(CreateProjectCommand createProjectCommand) {
    var prepared = blueprintMapper.prepare(createProjectCommand);
    var requested = (SpringBootJvmTarget) prepared.platform().platformTarget();
    var selection = bootVersions.select(requested.springBoot());
    var target =
        new SpringBootJvmTarget(requested.java(), requested.springBoot(), selection.patch());
    ProjectBlueprint blueprint = prepared.blueprint(target);

    String artifactId = blueprint.getMetadata().identity().artifactId().value();

    ProjectArtifactsPort artifactsPort =
        artifactsSelector.select(blueprint.getPlatform().techStack());

    var resources = artifactsPort.generate(blueprint);

    var published =
        publisher.publish(createProjectCommand.targetDirectory(), artifactId, resources);
    return responseMapper.from(
        blueprint, published.root(), published.files(), published.archive(), selection);
  }
}
