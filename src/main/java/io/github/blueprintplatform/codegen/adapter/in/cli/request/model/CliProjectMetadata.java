package io.github.blueprintplatform.codegen.adapter.in.cli.request.model;

public record CliProjectMetadata(
    String groupId,
    String artifactId,
    String projectName,
    String projectDescription,
    String packageName) {}
