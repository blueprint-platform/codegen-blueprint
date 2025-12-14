package io.github.blueprintplatform.codegen.application.port.in.project.dto;

import java.nio.file.Path;

public record GeneratedFileSummary(Path relativePath, boolean binary, boolean executable) {}
