package io.github.blueprintplatform.codegen.adapter.error.exception;

public final class InvalidCliOptionException extends AdapterException {

  public InvalidCliOptionException(String messageKey, Object... args) {
    super(messageKey, args);
  }

  public InvalidCliOptionException(String messageKey, Throwable cause, Object... args) {
    super(messageKey, cause, args);
  }
}
