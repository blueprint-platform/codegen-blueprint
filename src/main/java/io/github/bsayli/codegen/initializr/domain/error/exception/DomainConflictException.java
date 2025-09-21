package io.github.bsayli.codegen.initializr.domain.error.exception;

import io.github.bsayli.codegen.initializr.domain.error.code.ErrorCode;

public class DomainConflictException extends DomainException {
  public DomainConflictException(ErrorCode code, Object... args) {
    super(code, args);
  }
}
