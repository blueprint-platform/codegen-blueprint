package io.github.blueprintplatform.codegen.application.usecase.project;

import io.github.blueprintplatform.codegen.application.port.in.project.CreateProjectPort;
import io.github.blueprintplatform.codegen.application.error.exception.InvalidProjectRequestException;
import io.github.blueprintplatform.codegen.application.port.in.project.model.CreateProjectCommand;
import io.github.blueprintplatform.codegen.application.port.in.project.model.CreateProjectResult;
import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsSelector;
import io.github.blueprintplatform.codegen.application.port.out.ProjectPublisherPort;
import io.github.blueprintplatform.codegen.application.port.out.ProjectTargetResolver;
import io.github.blueprintplatform.codegen.application.usecase.project.mapper.CreateProjectResultMapper;
import io.github.blueprintplatform.codegen.application.usecase.project.mapper.ProjectBlueprintMapper;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;

public class CreateProjectHandler implements CreateProjectPort {

  private final ProjectBlueprintMapper blueprintMapper;
  private final CreateProjectResultMapper responseMapper;
  private final ProjectArtifactsSelector artifactsSelector;
  private final ProjectPublisherPort publisher;
  private final ProjectTargetResolver targetResolver;

  public CreateProjectHandler(
      ProjectBlueprintMapper blueprintMapper,
      CreateProjectResultMapper responseMapper,
      ProjectArtifactsSelector artifactsSelector,
      ProjectTargetResolver targetResolver,
      ProjectPublisherPort publisher) {
    this.blueprintMapper = blueprintMapper;
    this.responseMapper = responseMapper;
    this.artifactsSelector = artifactsSelector;
    this.targetResolver = targetResolver;
    this.publisher = publisher;
  }

  @Override
  public CreateProjectResult handle(CreateProjectCommand createProjectCommand) {
    var project = blueprintMapper.validate(createProjectCommand);
    var targetDirectory = createProjectCommand.targetDirectory();
    if (targetDirectory == null) {
      throw new InvalidProjectRequestException("application.project.target-directory.required");
    }
    var artifactsPort = artifactsSelector.select(project.platform().techStack());
    artifactsPort.validateMetadata(project.metadata());

    var resolution = targetResolver.resolve(project.platform().platformTarget());
    ProjectBlueprint blueprint = blueprintMapper.from(project, resolution.target());
    String artifactId = blueprint.getMetadata().identity().artifactId().value();

    var resources = artifactsPort.generate(blueprint);

    var published = publisher.publish(targetDirectory, artifactId, resources);
    return responseMapper.from(blueprint, published, resolution);
  }
}
