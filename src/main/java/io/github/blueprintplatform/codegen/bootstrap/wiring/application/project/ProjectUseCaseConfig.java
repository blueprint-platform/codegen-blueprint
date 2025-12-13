package io.github.blueprintplatform.codegen.bootstrap.wiring.application.project;

import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsSelector;
import io.github.blueprintplatform.codegen.application.port.out.archive.ProjectArchiverPort;
import io.github.blueprintplatform.codegen.application.usecase.project.*;
import io.github.blueprintplatform.codegen.domain.port.out.filesystem.ProjectFileListingPort;
import io.github.blueprintplatform.codegen.domain.port.out.filesystem.ProjectRootPort;
import io.github.blueprintplatform.codegen.domain.port.out.filesystem.ProjectWriterPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectUseCaseConfig {

  @Bean
  public ProjectBlueprintMapper projectBlueprintMapper() {
    return new ProjectBlueprintMapper();
  }

  @Bean
  public CreateProjectResultMapper createProjectResultMapper() {
    return new CreateProjectResultMapper();
  }

  @Bean
  public CreateProjectExecutionContext createProjectExecutionContext(
      ProjectRootPort rootPort,
      ProjectArtifactsSelector artifactsSelector,
      ProjectWriterPort writerPort,
      ProjectFileListingPort fileListingPort,
      ProjectArchiverPort archiverPort) {
    return new CreateProjectExecutionContext(
        rootPort, artifactsSelector, writerPort, fileListingPort, archiverPort);
  }

  @Bean
  public CreateProjectUseCase createProjectHandler(
      ProjectBlueprintMapper projectBlueprintMapper,
      CreateProjectResultMapper createProjectResultMapper,
      CreateProjectExecutionContext createProjectExecutionContext) {

    return new CreateProjectHandler(
        projectBlueprintMapper, createProjectResultMapper, createProjectExecutionContext);
  }
}
