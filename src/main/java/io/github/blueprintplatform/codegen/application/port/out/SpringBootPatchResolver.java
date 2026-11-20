package io.github.blueprintplatform.codegen.application.port.out;

import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;

public interface SpringBootPatchResolver {

  String resolveLatest(SpringBootVersion line);

  final class Unavailable extends RuntimeException {
    public Unavailable(String phase, Throwable cause) {
      super(phase + ": " + cause.getClass().getSimpleName(), cause);
    }
  }
}
