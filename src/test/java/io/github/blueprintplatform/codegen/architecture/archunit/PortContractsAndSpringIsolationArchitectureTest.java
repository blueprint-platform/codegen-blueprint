package io.github.blueprintplatform.codegen.architecture.archunit;

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
    packages = PortContractsAndSpringIsolationArchitectureTest.BASE_PACKAGE,
    importOptions = ImportOption.DoNotIncludeTests.class)
class PortContractsAndSpringIsolationArchitectureTest {

  static final String BASE_PACKAGE = "io.github.blueprintplatform.codegen";

  private static final String APPLICATION_INBOUND_PORTS = BASE_PACKAGE + ".application.port.in..";
  private static final String APPLICATION_OUTBOUND_PORTS = BASE_PACKAGE + ".application.port.out..";

  private static final String INBOUND_ADAPTERS = BASE_PACKAGE + ".adapter.in..";
  private static final String OUTBOUND_ADAPTERS = BASE_PACKAGE + ".adapter.out..";

  private static final String BOOTSTRAP = BASE_PACKAGE + ".bootstrap..";
  private static final String ADAPTERS = BASE_PACKAGE + ".adapter..";

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
          .resideInAnyPackage(BOOTSTRAP, ADAPTERS)
          .allowEmptyShould(true);

  private static final String PORT_SUFFIX = "Port";

  @ArchTest
  static final ArchRule application_ports_must_be_interfaces =
      classes()
          .that()
          .resideInAnyPackage(APPLICATION_INBOUND_PORTS, APPLICATION_OUTBOUND_PORTS)
          .and()
          .haveSimpleNameEndingWith(PORT_SUFFIX)
          .should()
          .beInterfaces()
          .allowEmptyShould(true);

  @ArchTest
  static final ArchRule adapters_must_not_define_ports =
      classes()
          .that()
          .resideInAnyPackage(INBOUND_ADAPTERS, OUTBOUND_ADAPTERS)
          .should()
          .haveSimpleNameNotEndingWith(PORT_SUFFIX)
          .allowEmptyShould(true);
}
