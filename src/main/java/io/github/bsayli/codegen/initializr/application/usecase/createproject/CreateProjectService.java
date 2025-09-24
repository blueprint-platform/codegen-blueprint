package io.github.bsayli.codegen.initializr.application.usecase.createproject;

import io.github.bsayli.codegen.initializr.domain.model.ProjectBlueprint;
import io.github.bsayli.codegen.initializr.domain.port.out.ProjectFileSystemPort;
import io.github.bsayli.codegen.initializr.domain.port.out.ProjectFileSystemPort.OnExistsPolicy;
import java.nio.file.Path;

public class CreateProjectService implements CreateProjectUseCase {

    private final ProjectBlueprintMapper mapper;
    private final ProjectFileSystemPort fs;

    public CreateProjectService(ProjectBlueprintMapper mapper, ProjectFileSystemPort fs) {
        this.mapper = mapper;
        this.fs = fs;
    }

    @Override
    public CreateProjectResult execute(CreateProjectCommand command) {
        ProjectBlueprint blueprint = mapper.toDomain(command);

        String artifactId = blueprint.getIdentity().artifactId().value();
        Path targetDirectory = command.targetDirectory();
        Path projectRoot = fs.prepareProjectRoot(targetDirectory, artifactId, OnExistsPolicy.FAIL_IF_EXISTS);

        return new CreateProjectResult(blueprint, projectRoot);
    }
}