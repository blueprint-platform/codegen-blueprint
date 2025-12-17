package io.github.blueprintplatform.codegen.architecture.archunit;

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
class DependencyDirectionArchitectureTest {

  private static final String APPLICATION_ROOT = "..application..";
  private static final String APPLICATION_PORT = "..application.port..";
  private static final String ADAPTER_ROOT = "..adapter..";
  @ArchTest
  static final ArchRule application_implementation_must_not_depend_on_adapters =
          noClasses()
                  .that()
                  .resideInAnyPackage(APPLICATION_ROOT)
                  .and()
                  .resideOutsideOfPackage(APPLICATION_PORT)
                  .should()
                  .dependOnClassesThat()
                  .resideInAnyPackage(ADAPTER_ROOT);
  @ArchTest
  static final ArchRule adapters_must_not_depend_on_application_implementation =
          noClasses()
                  .that()
                  .resideInAnyPackage(ADAPTER_ROOT)
                  .should()
                  .dependOnClassesThat(applicationImplementation());
  private static final String BOOTSTRAP_ROOT = "..bootstrap..";
  @ArchTest
  static final ArchRule bootstrap_must_not_be_depended_on =
          noClasses()
                  .that()
                  .resideOutsideOfPackage(BOOTSTRAP_ROOT)
                  .should()
                  .dependOnClassesThat()
                  .resideInAnyPackage(BOOTSTRAP_ROOT);

  private static com.tngtech.archunit.base.DescribedPredicate<JavaClass>
  applicationImplementation() {
    return describe(
            "reside in application but outside application.port",
            c -> c.getPackageName() != null
                    && c.getPackageName().contains(".application.")
                    && !c.getPackageName().contains(".application.port."));
  }
}