package io.github.blueprintplatform.codegen.application.port.in.project.dto;

import java.nio.file.Path;
import java.util.List;

public record CreateProjectResponse(
    ProjectGenerationSummary project,
    Path projectRoot,
    Path archivePath,
    List<GeneratedFileSummary> files) {}
