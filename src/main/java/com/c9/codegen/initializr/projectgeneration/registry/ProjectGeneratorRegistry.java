package com.c9.codegen.initializr.projectgeneration.registry;

import com.c9.codegen.initializr.projectgeneration.generator.ProjectGenerator;
import com.c9.codegen.initializr.projectgeneration.model.ProjectType;
import java.util.Optional;

public interface ProjectGeneratorRegistry {

  Optional<ProjectGenerator> getProjectGenerator(ProjectType projectType);
}
