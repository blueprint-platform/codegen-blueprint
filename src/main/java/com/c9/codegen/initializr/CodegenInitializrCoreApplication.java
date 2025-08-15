package com.c9.codegen.initializr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.c9.codegen.initializr")
public class CodegenInitializrCoreApplication {

  public static void main(String[] args) {
    SpringApplication.run(CodegenInitializrCoreApplication.class, args);
  }
}
