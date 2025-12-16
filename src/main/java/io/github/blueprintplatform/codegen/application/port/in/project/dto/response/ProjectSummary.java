package io.github.blueprintplatform.codegen.application.port.in.project.dto.response;

import io.github.blueprintplatform.codegen.application.port.in.project.dto.response.summary.*;
import java.util.List;

public record ProjectSummary(
        ProjectMetadataSummary metadata,
        TechStackSummary techStack,
        RuntimeTargetSummary runtimeTarget,
        ArchitectureSpecSummary architecture,
        List<ProjectDependencySummary> dependencies,
        List<ProjectFileSummary> files
) {}