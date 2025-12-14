package io.github.blueprintplatform.codegen.bootstrap.wiring.in.cli;

import io.github.blueprintplatform.codegen.adapter.in.cli.springboot.CreateProjectRequestMapper;
import io.github.blueprintplatform.codegen.adapter.in.cli.springboot.SpringBootGenerateCommand;
import io.github.blueprintplatform.codegen.application.port.in.project.CreateProjectPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBootCliConfig {

  @Bean
  public CreateProjectRequestMapper springBootCreateProjectRequestMapper() {
    return new CreateProjectRequestMapper();
  }

  @Bean
  public SpringBootGenerateCommand springBootGenerateCommand(
      CreateProjectRequestMapper mapper, CreateProjectPort createProjectPort) {

    return new SpringBootGenerateCommand(mapper, createProjectPort);
  }
}
