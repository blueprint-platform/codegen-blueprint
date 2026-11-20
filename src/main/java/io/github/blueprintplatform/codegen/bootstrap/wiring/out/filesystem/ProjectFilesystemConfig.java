package io.github.blueprintplatform.codegen.bootstrap.wiring.out.filesystem;

import io.github.blueprintplatform.codegen.adapter.out.filesystem.FileSystemProjectPublisher;
import io.github.blueprintplatform.codegen.application.port.out.ProjectPublisherPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectFilesystemConfig {
  @Bean
  ProjectPublisherPort projectPublisher() {
    return new FileSystemProjectPublisher();
  }
}
