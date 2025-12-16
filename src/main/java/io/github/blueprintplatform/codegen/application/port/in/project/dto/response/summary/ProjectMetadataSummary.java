package io.github.blueprintplatform.codegen.application.port.in.project.dto.response.summary;

public record ProjectMetadataSummary(
    String groupId,
    String artifactId,
    String projectName,
    String projectDescription,
    String packageName) {}
