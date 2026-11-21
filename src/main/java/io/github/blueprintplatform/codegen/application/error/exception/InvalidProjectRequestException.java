package io.github.blueprintplatform.codegen.application.error.exception;

public final class InvalidProjectRequestException extends ApplicationException {

  public InvalidProjectRequestException(String messageKey) {
    super(messageKey);
  }
}
