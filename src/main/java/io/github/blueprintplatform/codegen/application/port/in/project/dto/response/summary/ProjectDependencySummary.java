package io.github.blueprintplatform.codegen.application.port.in.project.dto.response.summary;

public record ProjectDependencySummary(
    String groupId, String artifactId, String version, String scope) {}
