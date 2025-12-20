package io.github.blueprintplatform.codegen.bootstrap.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "codegen")
public record CodegenProfilesProperties(@Valid @NotNull Map<String, ProfileProperties> profiles) {}
