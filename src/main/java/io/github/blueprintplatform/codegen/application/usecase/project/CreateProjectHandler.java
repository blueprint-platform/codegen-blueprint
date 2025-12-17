package io.github.blueprintplatform.codegen.application.usecase.project;

import static io.github.blueprintplatform.codegen.domain.port.out.filesystem.ProjectRootExistencePolicy.FAIL_IF_EXISTS;

import io.github.blueprintplatform.codegen.application.port.in.project.CreateProjectPort;
import io.github.blueprintplatform.codegen.application.port.in.project.model.CreateProjectCommand;
import io.github.blueprintplatform.codegen.application.port.in.project.model.CreateProjectResult;
import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsPort;
import io.github.blueprintplatform.codegen.application.port.out.output.ProjectOutputItem;
import io.github.blueprintplatform.codegen.application.usecase.project.context.CreateProjectExecutionContext;
import io.github.blueprintplatform.codegen.application.usecase.project.mapper.CreateProjectResponseMapper;
import io.github.blueprintplatform.codegen.application.usecase.project.mapper.ProjectBlueprintMapper;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import java.nio.file.Path;
import java.util.List;

public class CreateProjectHandler implements CreateProjectPort {

  private final ProjectBlueprintMapper blueprintMapper;
  private final CreateProjectResponseMapper responseMapper;
  private final CreateProjectExecutionContext executionContext;

  public CreateProjectHandler(
      ProjectBlueprintMapper blueprintMapper,
      CreateProjectResponseMapper responseMapper,
      CreateProjectExecutionContext executionContext) {
    this.blueprintMapper = blueprintMapper;
    this.responseMapper = responseMapper;
    this.executionContext = executionContext;
  }

  @Override
  public CreateProjectResult handle(CreateProjectCommand createProjectCommand) {
    ProjectBlueprint blueprint = blueprintMapper.from(createProjectCommand);

    String artifactId = blueprint.getMetadata().identity().artifactId().value();

    Path projectRoot =
        executionContext
            .rootPort()
            .prepareRoot(createProjectCommand.targetDirectory(), artifactId, FAIL_IF_EXISTS);

    ProjectArtifactsPort artifactsPort =
        executionContext.artifactsSelector().select(blueprint.getPlatform().techStack());

    var resources = artifactsPort.generate(blueprint);

    executionContext.writerPort().write(projectRoot, resources);

    Path archive = executionContext.archiverPort().archive(projectRoot, artifactId);

    List<ProjectOutputItem> projectOutputItems =
        executionContext.projectOutputPort().list(projectRoot);

    return responseMapper.from(blueprint, projectRoot, projectOutputItems, archive);
  }
}
