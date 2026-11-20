package io.github.blueprintplatform.codegen.application.port.out;

import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;

public interface SpringBootPatchResolver {

  String resolveLatest(SpringBootVersion line);
}
