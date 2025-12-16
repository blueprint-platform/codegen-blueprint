package io.github.blueprintplatform.codegen.adapter.error.exception;

public class InvalidDependencyAliasException extends AdapterException {

  private static final String KEY = "adapter.cli.springboot.dependency.unknown";

  public InvalidDependencyAliasException(String alias) {
    super(KEY, alias);
  }
}
