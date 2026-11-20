package io.github.blueprintplatform.codegen.bootstrap.wiring.application.project;

import io.github.blueprintplatform.codegen.application.port.in.project.CreateProjectPort;
import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsSelector;
import io.github.blueprintplatform.codegen.application.port.out.ProjectPublisherPort;
import io.github.blueprintplatform.codegen.application.port.out.SpringBootPatchResolver;
import io.github.blueprintplatform.codegen.application.usecase.project.BootVersionSelection;
import io.github.blueprintplatform.codegen.application.usecase.project.CreateProjectHandler;
import io.github.blueprintplatform.codegen.application.usecase.project.mapper.CreateProjectResultMapper;
import io.github.blueprintplatform.codegen.application.usecase.project.mapper.ProjectBlueprintMapper;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import java.util.Map;
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
  public BootVersionSelection bootVersionSelection(SpringBootPatchResolver resolver) {
    return new BootVersionSelection(
        resolver,
        Map.of(
            SpringBootVersion.V4_1, "4.1.3",
            SpringBootVersion.V4_2, "4.2.0"));
  }

  @Bean
  public CreateProjectPort createProjectHandler(
      ProjectBlueprintMapper projectBlueprintMapper,
      CreateProjectResultMapper createProjectResultMapper,
      ProjectArtifactsSelector artifactsSelector,
      BootVersionSelection bootVersions,
      ProjectPublisherPort publisher) {

    return new CreateProjectHandler(
        projectBlueprintMapper,
        createProjectResultMapper,
        artifactsSelector,
        bootVersions,
        publisher);
  }
}
