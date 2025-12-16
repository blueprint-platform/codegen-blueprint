package io.github.blueprintplatform.codegen.application.port.in.project.dto.response.summary;

public record TechStackSummary(
    String framework,
    String frameworkVersion,
    String buildTool,
    String buildToolVersion,
    String language,
    String languageVersion) {}
