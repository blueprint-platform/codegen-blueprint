package io.github.blueprintplatform.codegen.archunit;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "io.github.blueprintplatform.codegen",
    importOptions = ImportOption.DoNotIncludeTests.class)
class PortsImplementationIsolationArchitectureTest {

  private static final String ADAPTER = "..adapter..";

  @ArchTest
  static final ArchRule adapters_must_not_depend_on_application_implementation =
      noClasses()
          .that()
          .resideInAPackage(ADAPTER)
          .should()
          .dependOnClassesThat(
              describe(
                  "reside in application outside application.port",
                  PortsImplementationIsolationArchitectureTest::isApplicationImplementationType));

  private static boolean isApplicationImplementationType(JavaClass c) {
    String pkg = c.getPackageName();
    if (pkg == null || pkg.isBlank()) {
      return false;
    }
    return pkg.contains(".application.") && !pkg.contains(".application.port.");
  }
}
