package io.github.blueprintplatform.codegen.application.usecase.project.context;

import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsSelector;
import io.github.blueprintplatform.codegen.application.port.out.archive.ProjectArchiverPort;
import io.github.blueprintplatform.codegen.application.port.out.output.ProjectOutputPort;
import io.github.blueprintplatform.codegen.domain.port.out.filesystem.ProjectRootPort;
import io.github.blueprintplatform.codegen.domain.port.out.filesystem.ProjectWriterPort;

public record CreateProjectExecutionContext(
    ProjectRootPort rootPort,
    ProjectArtifactsSelector artifactsSelector,
    ProjectWriterPort writerPort,
    ProjectOutputPort projectOutputPort,
    ProjectArchiverPort archiverPort) {}
