package io.github.blueprintplatform.codegen.application.port.in.project.model;

import io.github.blueprintplatform.codegen.application.port.in.project.model.summary.*;
import java.util.List;

public record ProjectSummary(
        ProjectMetadataSummary metadata,
        TechStackSummary techStack,
        RuntimeTargetSummary runtimeTarget,
        ArchitectureSpecSummary architecture,
        List<ProjectDependencySummary> dependencies,
        List<ProjectFileSummary> files
) {}