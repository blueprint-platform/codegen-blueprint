package io.github.blueprintplatform.codegen.domain.port.out.artifact;

import java.nio.file.Path;

public sealed interface GeneratedResource
    permits GeneratedTextResource, GeneratedBinaryResource, GeneratedDirectory {

  Path relativePath();
}
