package io.github.blueprintplatform.codegen.architecture.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = PortsAndAdapterIsolationArchitectureTest.BASE_PACKAGE,
    importOptions = ImportOption.DoNotIncludeTests.class)
class PortsAndAdapterIsolationArchitectureTest {

  static final String BASE_PACKAGE = "io.github.blueprintplatform.codegen";

  private static final String INBOUND_ADAPTERS = BASE_PACKAGE + ".adapter.in..";
  private static final String OUTBOUND_ADAPTERS = BASE_PACKAGE + ".adapter.out..";

  @ArchTest
  static final ArchRule inbound_adapters_must_not_depend_on_outbound_adapters =
      noClasses()
          .that()
          .resideInAnyPackage(INBOUND_ADAPTERS)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(OUTBOUND_ADAPTERS)
          .allowEmptyShould(true);

  @ArchTest
  static final ArchRule outbound_adapters_must_not_depend_on_inbound_adapters =
      noClasses()
          .that()
          .resideInAnyPackage(OUTBOUND_ADAPTERS)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(INBOUND_ADAPTERS)
          .allowEmptyShould(true);
}
