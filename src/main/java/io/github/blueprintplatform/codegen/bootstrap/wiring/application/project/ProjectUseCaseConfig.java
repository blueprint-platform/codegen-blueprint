package io.github.blueprintplatform.codegen.bootstrap.wiring.application.project;

import io.github.blueprintplatform.codegen.application.port.in.project.CreateProjectPort;
import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsSelector;
import io.github.blueprintplatform.codegen.application.port.out.ProjectPublisherPort;
import io.github.blueprintplatform.codegen.application.port.out.ProjectTargetResolver;
import io.github.blueprintplatform.codegen.application.usecase.project.CreateProjectHandler;
import io.github.blueprintplatform.codegen.application.usecase.project.mapper.CreateProjectResultMapper;
import io.github.blueprintplatform.codegen.application.usecase.project.mapper.ProjectBlueprintMapper;
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
  public CreateProjectPort createProjectHandler(
      ProjectBlueprintMapper projectBlueprintMapper,
      CreateProjectResultMapper createProjectResultMapper,
      ProjectArtifactsSelector artifactsSelector,
      ProjectTargetResolver targetResolver,
      ProjectPublisherPort publisher) {

    return new CreateProjectHandler(
        projectBlueprintMapper,
        createProjectResultMapper,
        artifactsSelector,
        targetResolver,
        publisher);
  }
}
