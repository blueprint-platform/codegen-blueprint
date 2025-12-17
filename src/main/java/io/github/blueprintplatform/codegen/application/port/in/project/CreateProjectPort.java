package io.github.blueprintplatform.codegen.application.port.in.project;

import io.github.blueprintplatform.codegen.application.port.in.project.model.CreateProjectCommand;
import io.github.blueprintplatform.codegen.application.port.in.project.model.CreateProjectResult;

public interface CreateProjectPort {
  CreateProjectResult handle(CreateProjectCommand createProjectCommand);
}
