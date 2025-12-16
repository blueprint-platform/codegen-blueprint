package io.github.blueprintplatform.codegen.bootstrap.wiring.in.cli;

import io.github.blueprintplatform.codegen.adapter.in.cli.mapper.CreateProjectCommandMapper;
import io.github.blueprintplatform.codegen.adapter.in.cli.springboot.SpringBootGenerateCommand;
import io.github.blueprintplatform.codegen.application.port.in.project.CreateProjectPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBootCliConfig {

  @Bean
  public CreateProjectCommandMapper createProjectCommandMapper() {
    return new CreateProjectCommandMapper();
  }

  @Bean
  public SpringBootGenerateCommand springBootGenerateCommand(
      CreateProjectCommandMapper mapper, CreateProjectPort createProjectPort) {

    return new SpringBootGenerateCommand(mapper, createProjectPort);
  }
}
