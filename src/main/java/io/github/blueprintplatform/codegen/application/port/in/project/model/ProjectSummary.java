package io.github.blueprintplatform.codegen.application.port.in.project.model;

import io.github.blueprintplatform.codegen.application.port.in.project.model.summary.ArchitectureSpecSummary;
import io.github.blueprintplatform.codegen.application.port.in.project.model.summary.ProjectDependencySummary;
import io.github.blueprintplatform.codegen.application.port.in.project.model.summary.ProjectFileSummary;
import io.github.blueprintplatform.codegen.application.port.in.project.model.summary.ProjectMetadataSummary;
import io.github.blueprintplatform.codegen.domain.model.value.tech.PlatformSpec;
import java.util.List;

public record ProjectSummary(
    ProjectMetadataSummary metadata,
    PlatformSpec platform,
    ArchitectureSpecSummary architecture,
    List<ProjectDependencySummary> dependencies,
    List<ProjectFileSummary> files) {}
