package io.github.blueprintplatform.codegen.application.port.in.project.dto.response;

import java.nio.file.Path;

public record CreateProjectResult(ProjectSummary project, Path projectRoot, Path archivePath) {}
