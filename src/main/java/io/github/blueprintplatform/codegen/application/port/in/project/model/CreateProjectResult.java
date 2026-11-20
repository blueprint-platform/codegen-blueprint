package io.github.blueprintplatform.codegen.application.port.in.project.model;

import java.nio.file.Path;

public record CreateProjectResult(
    ProjectSummary project,
    Path projectRoot,
    Path archivePath,
    String versionSource,
    String versionWarning) {
  public CreateProjectResult(ProjectSummary project, Path projectRoot, Path archivePath) {
    this(project, projectRoot, archivePath, null, null);
  }
}
