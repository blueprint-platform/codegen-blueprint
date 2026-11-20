package io.github.blueprintplatform.codegen.bootstrap.wiring.out.version;

import io.github.blueprintplatform.codegen.adapter.out.version.CentralRepositoryHttp;
import io.github.blueprintplatform.codegen.adapter.out.version.CentralSpringBootPatchResolver;
import io.github.blueprintplatform.codegen.application.port.out.SpringBootPatchResolver;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class SpringBootPatchResolverConfig {
  private static final URI CENTRAL_PARENT_DIRECTORY =
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
  SpringBootPatchResolver springBootPatchResolver(
      @Qualifier("centralHttpClient") HttpClient client) {
    return new CentralSpringBootPatchResolver(
        new CentralRepositoryHttp(client, CENTRAL_PARENT_DIRECTORY));
  }
}
