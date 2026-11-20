package ${projectPackageName}.bp.sample.controller;

import ${projectPackageName}.bp.sample.controller.mapper.GreetingResponseMapper;
import ${projectPackageName}.bp.sample.domain.model.Greeting;
import ${projectPackageName}.bp.sample.domain.model.value.GreetingId;
import ${projectPackageName}.bp.sample.domain.model.value.GreetingText;
import ${projectPackageName}.bp.sample.service.GreetingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc(addFilters = false)
@org.springframework.context.annotation.Import(GreetingResponseMapper.class)
@WebMvcTest(controllers = GreetingController.class)
@Tag("integration")
class GreetingControllerWebMvcIT {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    GreetingService greetingService;

    @Test
    @DisplayName("GET /api/v1/sample/greetings/default -> 200 + body")
    void default_returns200() throws Exception {
        UUID id = UUID.randomUUID();

        Greeting greeting =
                Greeting.of(
                        new GreetingId(id),
                        GreetingText.of("Hello")
                );

        when(greetingService.getDefaultGreeting()).thenReturn(greeting);

        mvc.perform(get("/api/v1/sample/greetings/default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.text").value("Hello"));
    }

    @Test
    @DisplayName("GET /api/v1/sample/greetings?name=John -> 200 + body")
    void personal_returns200() throws Exception {
        UUID id = UUID.randomUUID();

        Greeting greeting =
                Greeting.of(
                        new GreetingId(id),
                        GreetingText.of("Hello, John!")
                );

        when(greetingService.getPersonalGreeting("John")).thenReturn(greeting);

        mvc.perform(get("/api/v1/sample/greetings").param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.text").value("Hello, John!"));
    }
}