package io.github.blueprintplatform.codegen.application.port.out.output;

import java.nio.file.Path;

public record ProjectOutputItem(Path relativePath, boolean binary, boolean executable) {}
