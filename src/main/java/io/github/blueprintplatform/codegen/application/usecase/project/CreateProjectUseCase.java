package io.github.blueprintplatform.codegen.application.usecase.project;

import io.github.blueprintplatform.codegen.application.usecase.project.model.CreateProjectCommand;
import io.github.blueprintplatform.codegen.application.usecase.project.model.CreateProjectResult;

public interface CreateProjectUseCase {
  CreateProjectResult handle(CreateProjectCommand command);
}
