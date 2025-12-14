package io.github.blueprintplatform.codegen.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@AnalyzeClasses(
    packages = "io.github.blueprintplatform.codegen",
    importOptions = ImportOption.DoNotIncludeTests.class)
class PortContractsAndSpringIsolationArchitectureTest {

  @ArchTest
  static final ArchRule application_ports_must_be_interfaces =
      classes()
          .that()
          .resideInAnyPackage("..application.port.in..", "..application.port.out..")
          .and()
          .haveSimpleNameEndingWith("Port")
          .should()
          .beInterfaces();

  @ArchTest
  static final ArchRule adapters_must_not_define_ports =
      classes()
          .that()
          .resideInAnyPackage("..adapter.in..", "..adapter.out..")
          .should()
          .haveSimpleNameNotEndingWith("Port");

  @ArchTest
  static final ArchRule spring_stereotypes_must_live_in_bootstrap_or_adapters_only =
      classes()
          .that()
          .areAnnotatedWith(Component.class)
          .or()
          .areAnnotatedWith(Service.class)
          .or()
          .areAnnotatedWith(Repository.class)
          .or()
          .areAnnotatedWith(Configuration.class)
          .or()
          .areAnnotatedWith(ConfigurationProperties.class)
          .should()
          .resideInAnyPackage("..bootstrap..", "..adapter..");
}
