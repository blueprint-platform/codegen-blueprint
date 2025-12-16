package io.github.blueprintplatform.codegen.application.port.in.project;

import io.github.blueprintplatform.codegen.application.port.in.project.dto.request.CreateProjectCommand;
import io.github.blueprintplatform.codegen.application.port.in.project.dto.response.CreateProjectResult;

public interface CreateProjectPort {
  CreateProjectResult handle(CreateProjectCommand createProjectCommand);
}
