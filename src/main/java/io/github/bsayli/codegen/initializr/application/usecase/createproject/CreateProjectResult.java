package io.github.bsayli.codegen.initializr.application.usecase.createproject;

import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import java.nio.file.Path;

public record CreateProjectResult(ProjectBlueprint blueprint, Path projectRoot) {}
