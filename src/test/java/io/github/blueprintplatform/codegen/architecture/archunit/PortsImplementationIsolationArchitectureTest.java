package io.github.blueprintplatform.codegen.architecture.archunit;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = PortsImplementationIsolationArchitectureTest.BASE_PACKAGE,
    importOptions = ImportOption.DoNotIncludeTests.class)
class PortsImplementationIsolationArchitectureTest {

  static final String BASE_PACKAGE = "io.github.blueprintplatform.codegen";

  private static final String ADAPTERS = BASE_PACKAGE + ".adapter..";

  private static final String APPLICATION_PREFIX = BASE_PACKAGE + ".application.";
  private static final String APPLICATION_PORT_PREFIX = BASE_PACKAGE + ".application.port.";

  @ArchTest
  static final ArchRule adapters_must_not_depend_on_application_implementation =
      noClasses()
          .that()
          .resideInAnyPackage(ADAPTERS)
          .should()
          .dependOnClassesThat(applicationImplementationTypes())
          .allowEmptyShould(true);

  private static com.tngtech.archunit.base.DescribedPredicate<JavaClass>
      applicationImplementationTypes() {
    return describe(
        "application implementation types (application but outside application.port)",
        PortsImplementationIsolationArchitectureTest::isApplicationImplementationType);
  }

  private static boolean isApplicationImplementationType(JavaClass c) {
    String pkg = c.getPackageName();
    if (pkg == null || pkg.isBlank()) {
      return false;
    }
    return pkg.startsWith(APPLICATION_PREFIX) && !pkg.startsWith(APPLICATION_PORT_PREFIX);
  }
}
