package io.github.blueprintplatform.codegen.bootstrap.wiring.out.version;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.blueprintplatform.codegen.application.port.out.ProjectTargetResolver;
import java.net.http.HttpClient;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class SpringBootTargetResolverConfigTest {
  @Test
  void contextOwnsTheCentralClientLifecycle() {
    HttpClient client;
    try (var context =
        new AnnotationConfigApplicationContext(SpringBootTargetResolverConfig.class)) {
      client = context.getBean("centralHttpClient", HttpClient.class);
      assertThat(context.getBean(ProjectTargetResolver.class)).isNotNull();
      assertThat(client.connectTimeout()).contains(Duration.ofSeconds(3));
      assertThat(client.followRedirects()).isEqualTo(HttpClient.Redirect.NEVER);
      assertThat(client.isTerminated()).isFalse();
    }
    assertThat(client.isTerminated()).isTrue();
  }
}
