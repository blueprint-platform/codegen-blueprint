package io.github.blueprintplatform.codegen.application.usecase.project;

import static io.github.blueprintplatform.codegen.domain.port.out.filesystem.ProjectRootExistencePolicy.FAIL_IF_EXISTS;

import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsPort;
import io.github.blueprintplatform.codegen.application.usecase.project.model.CreateProjectCommand;
import io.github.blueprintplatform.codegen.application.usecase.project.model.CreateProjectResult;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import java.nio.file.Path;
import java.util.List;

public class CreateProjectHandler implements CreateProjectUseCase {

  private final ProjectBlueprintMapper blueprintMapper;
  private final CreateProjectResultMapper resultMapper;
  private final CreateProjectExecutionContext executionContext;

  public CreateProjectHandler(
      ProjectBlueprintMapper blueprintMapper,
      CreateProjectResultMapper resultMapper,
      CreateProjectExecutionContext executionContext) {
    this.blueprintMapper = blueprintMapper;
    this.resultMapper = resultMapper;
    this.executionContext = executionContext;
  }

  @Override
  public CreateProjectResult handle(CreateProjectCommand command) {
    ProjectBlueprint projectBlueprint = blueprintMapper.from(command);

    Path projectRoot =
        executionContext
            .rootPort()
            .prepareRoot(
                command.targetDirectory(),
                projectBlueprint.getIdentity().artifactId().value(),
                FAIL_IF_EXISTS);

    ProjectArtifactsPort artifactsPort =
        executionContext.artifactsSelector().select(projectBlueprint.getTechStack());
    var resources = artifactsPort.generate(projectBlueprint);

    executionContext.writerPort().write(projectRoot, resources);

    String baseName = projectBlueprint.getIdentity().artifactId().value();
    Path archive = executionContext.archiverPort().archive(projectRoot, baseName);

    List<Path> projectFiles = executionContext.fileListingPort().listFiles(projectRoot);

    return resultMapper.from(projectBlueprint, projectRoot, projectFiles, archive);
  }
}
