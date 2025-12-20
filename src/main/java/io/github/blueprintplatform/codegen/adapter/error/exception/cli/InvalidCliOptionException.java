package io.github.blueprintplatform.codegen.adapter.error.exception.cli;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;

public final class InvalidCliOptionException extends AdapterException {

  public InvalidCliOptionException(String messageKey, Object... args) {
    super(messageKey, args);
  }

  public InvalidCliOptionException(String messageKey, Throwable cause, Object... args) {
    super(messageKey, cause, args);
  }
}
