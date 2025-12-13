package io.github.blueprintplatform.codegen.application.usecase.project.model;

import java.nio.file.Path;
import java.util.List;

public record CreateProjectResult(
    ProjectGenerationSummary project,
    Path projectRoot,
    Path archivePath,
    List<GeneratedFileSummary> files) {}
