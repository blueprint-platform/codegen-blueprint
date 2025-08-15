package com.c9.codegen.initializr.projectgeneration.ports;

import com.c9.codegen.initializr.projectgeneration.model.ProjectMetadata;
import java.io.File;
import java.io.IOException;

public interface ProjectBuildWrapperGenerator {

  void generateBuildWrapper(File projectDestination, ProjectMetadata projectMetadata)
      throws IOException;
}
