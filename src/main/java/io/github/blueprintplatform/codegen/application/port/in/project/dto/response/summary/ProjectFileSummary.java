package io.github.blueprintplatform.codegen.application.port.in.project.dto.response.summary;

import java.nio.file.Path;

public record ProjectFileSummary(Path relativePath, boolean binary, boolean executable) {}
