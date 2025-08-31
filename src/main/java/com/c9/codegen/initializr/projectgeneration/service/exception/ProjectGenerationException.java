package com.c9.codegen.initializr.projectgeneration.service.exception;

public class ProjectGenerationException extends RuntimeException {

  public ProjectGenerationException(String message) {
    super(message);
  }

  public ProjectGenerationException(String message, Throwable cause) {
    super(message, cause);
  }
}
