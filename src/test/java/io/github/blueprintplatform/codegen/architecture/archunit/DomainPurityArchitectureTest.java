package io.github.blueprintplatform.codegen.architecture.archunit;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = DomainPurityArchitectureTest.BASE_PACKAGE,
    importOptions = ImportOption.DoNotIncludeTests.class)
class DomainPurityArchitectureTest {

  static final String BASE_PACKAGE = "io.github.blueprintplatform.codegen";

  private static final String DOMAIN_PACKAGE_PATTERN = BASE_PACKAGE + ".domain..";
  private static final String DOMAIN_ROOT_PREFIX = BASE_PACKAGE + ".domain.";

  @ArchTest
  static final ArchRule domain_must_depend_only_on_jdk_and_domain =
      noClasses()
          .that()
          .resideInAnyPackage(DOMAIN_PACKAGE_PATTERN)
          .should()
          .dependOnClassesThat(
              describe(
                  "reside outside domain and are not JDK types",
                  DomainPurityArchitectureTest::isForbiddenForDomain))
          .allowEmptyShould(true);

  private static boolean isForbiddenForDomain(JavaClass c) {
    return !isAllowedForDomain(c);
  }

  private static boolean isAllowedForDomain(JavaClass c) {
    String pkg = c.getPackageName();

    if (pkg == null || pkg.isBlank()) {
      return true;
    }

    if (pkg.startsWith("java.")) {
      return true;
    }

    return pkg.startsWith(DOMAIN_ROOT_PREFIX);
  }
}
