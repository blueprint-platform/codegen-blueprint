package io.github.blueprintplatform.codegen.application.usecase.project.model;

public record ProjectDependencySummary(
    String groupId, String artifactId, String version, String scope) {}
