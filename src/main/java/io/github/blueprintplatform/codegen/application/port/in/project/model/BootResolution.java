package io.github.blueprintplatform.codegen.application.port.in.project.model;

public record BootResolution(String patch, Source source, String warning) {
  public enum Source {
    CENTRAL_LATEST,
    BUNDLED_FALLBACK
  }
}
