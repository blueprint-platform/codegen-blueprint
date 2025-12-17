package io.github.blueprintplatform.codegen.application.port.in.project.model.summary;

public record ProjectMetadataSummary(
    String groupId,
    String artifactId,
    String projectName,
    String projectDescription,
    String packageName) {}
