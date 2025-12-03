package io.github.bsayli.codegen.initializr.bootstrap.wiring.cli;

import io.github.bsayli.codegen.initializr.adapter.in.cli.CodegenCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CliCommonConfig {

  @Bean
  public CodegenCommand codegenCommand() {
    return new CodegenCommand();
  }
}
