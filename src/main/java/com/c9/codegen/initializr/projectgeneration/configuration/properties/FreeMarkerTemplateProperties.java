package com.c9.codegen.initializr.projectgeneration.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "freemarker")
public record FreeMarkerTemplateProperties(
    String encoding, String templateExceptionHandler, String templatePath) {}
