package io.github.bsayli.codegen.initializr.application.usecase.createproject;

public interface CreateProjectUseCase {
  CreateProjectResult handle(CreateProjectCommand command);
}
