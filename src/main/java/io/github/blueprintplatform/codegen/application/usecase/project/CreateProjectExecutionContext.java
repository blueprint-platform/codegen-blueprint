package io.github.blueprintplatform.codegen.application.usecase.project;

import io.github.blueprintplatform.codegen.application.port.out.ProjectArtifactsSelector;
import io.github.blueprintplatform.codegen.application.port.out.archive.ProjectArchiverPort;
import io.github.blueprintplatform.codegen.domain.port.out.filesystem.ProjectFileListingPort;
import io.github.blueprintplatform.codegen.domain.port.out.filesystem.ProjectRootPort;
import io.github.blueprintplatform.codegen.domain.port.out.filesystem.ProjectWriterPort;

public record CreateProjectExecutionContext(
    ProjectRootPort rootPort,
    ProjectArtifactsSelector artifactsSelector,
    ProjectWriterPort writerPort,
    ProjectFileListingPort fileListingPort,
    ProjectArchiverPort archiverPort) {}
