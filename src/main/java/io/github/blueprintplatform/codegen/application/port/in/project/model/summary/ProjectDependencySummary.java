package io.github.blueprintplatform.codegen.application.port.in.project.model.summary;

public record ProjectDependencySummary(
    String groupId, String artifactId, String version, String scope) {}
