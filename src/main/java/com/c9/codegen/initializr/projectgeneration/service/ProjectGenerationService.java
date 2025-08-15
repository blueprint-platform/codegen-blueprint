package com.c9.codegen.initializr.projectgeneration.service;

import com.c9.codegen.initializr.projectgeneration.model.ProjectMetadata;
import com.c9.codegen.initializr.projectgeneration.model.ProjectType;
import java.io.IOException;
import java.nio.file.Path;

public interface ProjectGenerationService {

  Path generateProject(ProjectType projectType, ProjectMetadata projectMetadata) throws IOException;
}
