package io.github.blueprintplatform.codegen.application.usecase.project.model;

import io.github.blueprintplatform.codegen.domain.model.value.layout.ProjectLayout;
import io.github.blueprintplatform.codegen.domain.model.value.sample.SampleCodeOptions;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.PlatformTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.TechStack;
import java.util.List;

public record ProjectGenerationSummary(
    String groupId,
    String artifactId,
    String projectName,
    String projectDescription,
    String packageName,
    TechStack techStack,
    ProjectLayout layout,
    PlatformTarget platformTarget,
    SampleCodeOptions sampleCode,
    List<ProjectDependencySummary> dependencies) {}
