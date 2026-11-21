package io.github.blueprintplatform.codegen.bootstrap.wiring.in.cli;

import io.github.blueprintplatform.codegen.adapter.error.exception.base.AdapterException;
import io.github.blueprintplatform.codegen.application.error.exception.ApplicationException;
import io.github.blueprintplatform.codegen.bootstrap.error.exception.BootstrapException;
import io.github.blueprintplatform.codegen.domain.error.exception.DomainException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

public class CodegenCliExceptionHandler implements IExecutionExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(CodegenCliExceptionHandler.class);

  private final MessageSource messageSource;

  public CodegenCliExceptionHandler(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public int handleExecutionException(Exception ex, CommandLine cmd, ParseResult parseResult) {

    if (ex instanceof ParameterException parameterException) {
      cmd.getErr().println("codegen: usage error: " + parameterException.getMessage());
      cmd.usage(cmd.getErr());
      return 1;
    }

    Throwable cause =
        ex instanceof DomainException
                || ex instanceof ApplicationException
                || ex instanceof AdapterException
                || ex instanceof BootstrapException
                || ex.getCause() == null
            ? ex
            : ex.getCause();

    return switch (cause) {
      case DomainException domainException -> {
        printLocalizedError(cmd, domainException.getMessageKey(), domainException.getArgs());
        yield 1;
      }
      case ApplicationException applicationException -> {
        printLocalizedError(
            cmd, applicationException.getMessageKey(), applicationException.getArgs());
        yield 2;
      }
      case AdapterException adapterException -> {
        printLocalizedError(cmd, adapterException.getMessageKey(), adapterException.getArgs());
        printCleanupFailures(cmd, adapterException);
        if (adapterException.getCause() != null) {
          printCleanupFailures(cmd, adapterException.getCause());
        }
        yield 3;
      }
      case BootstrapException bootstrapException -> {
        printLocalizedError(cmd, bootstrapException.getMessageKey(), bootstrapException.getArgs());
        yield 3;
      }
      case IOException ioException -> {
        cmd.getErr().println("codegen: error: I/O error occurred: " + ioException.getMessage());
        log.error("I/O error in CLI execution", ioException);
        yield 3;
      }
      default -> {
        log.error("Unexpected CLI error", cause);
        cmd.getErr()
            .println("codegen: error: Unexpected failure. Please try again or open an issue.");
        yield 99;
      }
    };
  }

  private void printCleanupFailures(CommandLine cmd, Throwable failure) {
    for (Throwable suppressed : failure.getSuppressed()) {
      Object detail = suppressed instanceof AdapterException ? suppressed : suppressed.getMessage();
      cmd.getErr().println("codegen: cleanup: " + localizeArgument(detail));
    }
  }

  private void printLocalizedError(CommandLine cmd, String key, Object[] args) {
    cmd.getErr().println("codegen: error: " + localize(key, args));
    cmd.getErr().println("(code: " + key + ")");
  }

  private String localize(String key, Object[] args) {
    Object[] localized =
        args == null ? null : Arrays.stream(args).map(this::localizeArgument).toArray();
    return messageSource.getMessage(key, localized, key, Locale.getDefault());
  }

  private Object localizeArgument(Object argument) {
    if (argument instanceof AdapterException failure) {
      return localize(failure.getMessageKey(), failure.getArgs());
    }
    return argument;
  }
}
