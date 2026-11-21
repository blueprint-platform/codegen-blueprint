package io.github.blueprintplatform.codegen.bootstrap.wiring.out.version;

import io.github.blueprintplatform.codegen.adapter.out.version.SpringBootTargetResolver;
import io.github.blueprintplatform.codegen.application.port.out.ProjectTargetResolver;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class SpringBootTargetResolverConfig {
  private static final URI MAVEN_CENTRAL_PARENT_DIRECTORY =
      URI.create(
          "https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-parent/");

  @Bean(destroyMethod = "close")
  HttpClient centralHttpClient() {
    return HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(3))
        .followRedirects(HttpClient.Redirect.NEVER)
        .build();
  }

  @Bean
  ProjectTargetResolver springBootTargetResolver(
      @Qualifier("centralHttpClient") HttpClient client) {
    return new SpringBootTargetResolver(
        client,
        MAVEN_CENTRAL_PARENT_DIRECTORY,
        Map.of(SpringBootVersion.V4_1, "4.1.3", SpringBootVersion.V4_2, "4.2.0"));
  }
}
