package com.c9.codegen.initializr.projectgeneration.registry;

import com.c9.codegen.initializr.projectgeneration.generator.ProjectGenerator;
import com.c9.codegen.initializr.projectgeneration.model.ProjectType;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class SimpleProjectGeneratorRegistry implements ProjectGeneratorRegistry {

  private final Map<ProjectType, ProjectGenerator> registeredProjectGenerators;

  public SimpleProjectGeneratorRegistry(
      Map<ProjectType, ProjectGenerator> registeredProjectGenerators) {
    this.registeredProjectGenerators = registeredProjectGenerators;
  }

  @Override
  public Optional<ProjectGenerator> getProjectGenerator(ProjectType projectType) {
    return Optional.ofNullable(registeredProjectGenerators.get(projectType));
  }
}
