package ${projectPackageName}.bp.sample.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.security.user.name=sample", "spring.security.user.password=sample-test"})
class GreetingSampleHttpIT {
    @LocalServerPort
    int port;

    @Autowired(required = false)
    DataSource dataSource;

    @Test
    void selectedInfrastructureIsUsable() throws Exception {
        if (ClassUtils.isPresent("org.springframework.security.web.SecurityFilterChain",
                getClass().getClassLoader())) {
            try (var client = HttpClient.newHttpClient()) {
                assertThat(client.send(request("/default").build(),
                        HttpResponse.BodyHandlers.discarding()).statusCode()).isEqualTo(401);
            }
        }
        if (dataSource != null) {
            try (var connection = dataSource.getConnection()) {
                assertThat(connection.getMetaData().getURL()).startsWith("jdbc:h2:");
            }
        }
    }

    @Test
    void defaultEndpoint() throws Exception {
        assertGreeting("/default", "Hello from layered sample!");
    }

    @Test
    void personalizedEndpoint() throws Exception {
        assertGreeting("?name=John", "Hello, John!");
    }

    @Test
    void missingNameFallsBack() throws Exception {
        assertGreeting("", "Hello from layered sample!");
    }

    private void assertGreeting(String suffix, String expected) throws Exception {
        var request = request(suffix)
                .header("Authorization", "Basic c2FtcGxlOnNhbXBsZS10ZXN0").build();
        try (var client = HttpClient.newHttpClient()) {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.headers().firstValue("Content-Type").orElse("")).startsWith("application/json");
            var json = tools.jackson.databind.json.JsonMapper.builder().build().readTree(response.body());
            assertThat(json.path("id").asString()).isNotBlank();
            assertThat(json.path("text").asString()).isEqualTo(expected);
        }
    }

    private HttpRequest.Builder request(String suffix) {
        return HttpRequest.newBuilder(
                        URI.create("http://localhost:" + port + "/api/v1/sample/greetings" + suffix))
                .timeout(Duration.ofSeconds(10));
    }
}
