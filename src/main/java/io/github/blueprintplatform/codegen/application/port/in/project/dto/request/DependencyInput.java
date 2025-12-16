package io.github.blueprintplatform.codegen.application.port.in.project.dto.request;

public record DependencyInput(String groupId, String artifactId, String version, String scope) {}
