package io.github.blueprintplatform.codegen.application.usecase.project.model;

public record DependencyInput(String groupId, String artifactId, String version, String scope) {}
