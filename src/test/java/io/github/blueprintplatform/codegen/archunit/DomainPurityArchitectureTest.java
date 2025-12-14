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
class DomainPurityArchitectureTest {

  private static final String DOMAIN = "..domain..";

  @ArchTest
  static final ArchRule domain_must_depend_only_on_jdk_and_domain =
      noClasses()
          .that()
          .resideInAPackage(DOMAIN)
          .should()
          .dependOnClassesThat(
              describe(
                  "reside outside domain and are not JDK types",
                  (JavaClass c) -> !isAllowedForDomain(c)));

  private static boolean isAllowedForDomain(JavaClass c) {
    String pkg = c.getPackageName();

    if (pkg == null || pkg.isBlank()) {
      return true;
    }

    if (pkg.startsWith("java.") || pkg.startsWith("javax.")) {
      return true;
    }

    if (pkg.startsWith("com.tngtech.archunit.")) {
      return true;
    }

    return pkg.contains(".domain.");
  }
}
