package io.github.blueprintplatform.codegen.application.port.in.project.model;

import io.github.blueprintplatform.codegen.application.port.model.TargetResolution;
import java.nio.file.Path;

public record CreateProjectResult(
    ProjectSummary project, Path projectRoot, Path archivePath, TargetResolution targetResolution) {}
