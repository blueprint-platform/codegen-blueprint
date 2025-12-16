package io.github.blueprintplatform.codegen.bootstrap.wiring.in.cli;

import io.github.blueprintplatform.codegen.adapter.in.cli.CodegenCommand;
import io.github.blueprintplatform.codegen.adapter.in.cli.springboot.option.*;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
public class CodegenCliExecutor {

  private final CodegenCommand codegenCommand;
  private final CommandLine.IFactory factory;
  private final CodegenCliExceptionHandler exceptionHandler;

  public CodegenCliExecutor(
      CodegenCommand codegenCommand,
      CommandLine.IFactory factory,
      CodegenCliExceptionHandler exceptionHandler) {

    this.codegenCommand = codegenCommand;
    this.factory = factory;
    this.exceptionHandler = exceptionHandler;
  }

  public int execute(String[] args) {
    CommandLine cmd =
        new CommandLine(codegenCommand, factory)
            .registerConverter(SpringBootBuildToolOption.class, SpringBootBuildToolOption::fromKey)
            .registerConverter(SpringBootLanguageOption.class, SpringBootLanguageOption::fromKey)
            .registerConverter(
                SpringBootJavaVersionOption.class, SpringBootJavaVersionOption::fromKey)
            .registerConverter(SpringBootVersionOption.class, SpringBootVersionOption::fromKey)
            .registerConverter(SpringBootLayoutOption.class, SpringBootLayoutOption::fromKey)
            .registerConverter(
                SpringBootArchitectureEnforcementOption.class,
                SpringBootArchitectureEnforcementOption::fromKey)
            .registerConverter(
                SpringBootSampleCodeOption.class, SpringBootSampleCodeOption::fromKey)
            .registerConverter(
                SpringBootDependencyOption.class, SpringBootDependencyOption::fromKey);

    cmd.setExecutionExceptionHandler(exceptionHandler);

    return cmd.execute(args);
  }
}
