package io.github.blueprintplatform.codegen.bootstrap.wiring.application.project;

import io.github.blueprintplatform.codegen.application.port.in.project.CreateProjectPort;
import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsSelector;
import io.github.blueprintplatform.codegen.application.port.out.archive.ProjectArchiverPort;
import io.github.blueprintplatform.codegen.application.port.out.output.ProjectOutputPort;
import io.github.blueprintplatform.codegen.application.usecase.project.*;
import io.github.blueprintplatform.codegen.application.usecase.project.context.CreateProjectExecutionContext;
import io.github.blueprintplatform.codegen.application.usecase.project.mapper.CreateProjectResultMapper;
import io.github.blueprintplatform.codegen.application.usecase.project.mapper.ProjectBlueprintMapper;
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
      ProjectOutputPort projectOutputPort,
      ProjectArchiverPort archiverPort) {
    return new CreateProjectExecutionContext(
        rootPort, artifactsSelector, writerPort, projectOutputPort, archiverPort);
  }

  @Bean
  public CreateProjectPort createProjectHandler(
      ProjectBlueprintMapper projectBlueprintMapper,
      CreateProjectResultMapper createProjectResultMapper,
      CreateProjectExecutionContext createProjectExecutionContext) {

    return new CreateProjectHandler(
        projectBlueprintMapper, createProjectResultMapper, createProjectExecutionContext);
  }
}
