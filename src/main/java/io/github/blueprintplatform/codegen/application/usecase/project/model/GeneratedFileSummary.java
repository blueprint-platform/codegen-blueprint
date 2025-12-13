package io.github.blueprintplatform.codegen.application.usecase.project.model;

import java.nio.file.Path;

public record GeneratedFileSummary(Path relativePath, boolean binary, boolean executable) {}
