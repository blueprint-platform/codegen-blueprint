package io.github.bsayli.codegen.initializr.projectgeneration.registry.configuration;

import io.github.bsayli.codegen.initializr.domain.model.value.tech.options.BuildTool;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.options.Framework;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.options.Language;
import io.github.bsayli.codegen.initializr.projectgeneration.generator.ProjectGenerator;
import io.github.bsayli.codegen.initializr.projectgeneration.model.ProjectType;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectGeneratorRegistryConfiguration {

  private final ProjectGenerator springBootMavenJavaProjectGenerator;

  public ProjectGeneratorRegistryConfiguration(
      ProjectGenerator springBootMavenJavaProjectGenerator) {
    this.springBootMavenJavaProjectGenerator = springBootMavenJavaProjectGenerator;
  }

  @Bean
  Map<ProjectType, ProjectGenerator> registeredProjectGenerators() {
    Map<ProjectType, ProjectGenerator> generatorFactories = new HashMap<>();

    ProjectType springBootMavenJavaProjectType =
        new ProjectType(Framework.SPRING_BOOT, BuildTool.MAVEN, Language.JAVA);

    generatorFactories.put(springBootMavenJavaProjectType, springBootMavenJavaProjectGenerator);

    return generatorFactories;
  }
}
