package io.github.blueprintplatform.codegen.application.port.in.project.model.summary;

public record TechStackSummary(
    String framework,
    String frameworkVersion,
    String buildTool,
    String buildToolVersion,
    String language,
    String languageVersion) {}
