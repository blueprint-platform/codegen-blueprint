package io.github.blueprintplatform.codegen.application.port.in.project;

import io.github.blueprintplatform.codegen.application.port.in.project.dto.CreateProjectRequest;
import io.github.blueprintplatform.codegen.application.port.in.project.dto.CreateProjectResponse;

public interface CreateProjectPort {
  CreateProjectResponse handle(CreateProjectRequest createProjectRequest);
}
