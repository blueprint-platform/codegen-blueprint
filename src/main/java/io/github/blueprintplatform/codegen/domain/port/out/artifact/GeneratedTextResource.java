package io.github.blueprintplatform.codegen.domain.port.out.artifact;

import static io.github.blueprintplatform.codegen.domain.policy.file.GeneratedFilePolicy.*;

import java.nio.charset.Charset;
import java.nio.file.Path;

public record GeneratedTextResource(Path relativePath, String content, Charset charset)
    implements GeneratedResource {

  public GeneratedTextResource {
    requireRelativePath(relativePath);
    requireTextContent(content, charset);
  }
}
