package io.github.blueprintplatform.codegen.architecture.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = InboundAdapterDomainServiceIsolationArchitectureTest.BASE_PACKAGE,
    importOptions = ImportOption.DoNotIncludeTests.class)
class InboundAdapterDomainServiceIsolationArchitectureTest {

  static final String BASE_PACKAGE = "io.github.blueprintplatform.codegen";

  private static final String INBOUND_ADAPTERS = "..adapter.in..";
  private static final String DOMAIN_SERVICES = BASE_PACKAGE + ".domain.service..";

  @ArchTest
  static final ArchRule inbound_adapters_must_not_depend_on_domain_services =
      noClasses()
          .that()
          .resideInAnyPackage(INBOUND_ADAPTERS)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(DOMAIN_SERVICES)
          .allowEmptyShould(true);
}
