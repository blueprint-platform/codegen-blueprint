package io.github.blueprintplatform.codegen.architecture.archunit;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = CliContractsIsolationArchitectureTest.BASE_PACKAGE,
    importOptions = ImportOption.DoNotIncludeTests.class)
class CliContractsIsolationArchitectureTest {

  static final String BASE_PACKAGE = "io.github.blueprintplatform.codegen";

  private static final String DOMAIN_PATTERN = BASE_PACKAGE + ".domain..";

  private static final String CLI_ROOT = BASE_PACKAGE + ".adapter.in.cli..";

  private static final String CLI_REQUEST = BASE_PACKAGE + ".adapter.in.cli.request..";
  private static final String CLI_REQUEST_MODEL = BASE_PACKAGE + ".adapter.in.cli.request.model..";
  private static final String CLI_SHARED = BASE_PACKAGE + ".adapter.in.cli.shared..";
  private static final String CLI_SPRINGBOOT = BASE_PACKAGE + ".adapter.in.cli.springboot..";
  private static final String CLI_OPTIONS = BASE_PACKAGE + ".adapter.in.cli.springboot.option..";

  @ArchTest
  static final ArchRule cli_transport_contracts_must_not_depend_on_domain =
      noClasses()
          .that()
          .resideInAnyPackage(CLI_REQUEST, CLI_REQUEST_MODEL, CLI_SHARED, CLI_OPTIONS)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(DOMAIN_PATTERN)
          .allowEmptyShould(true);

  private static final String CLI_MAPPER = BASE_PACKAGE + ".adapter.in.cli.mapper..";

  @ArchTest
  static final ArchRule cli_commands_must_not_depend_on_domain_directly =
      noClasses()
          .that()
          .resideInAnyPackage(CLI_SPRINGBOOT)
          .and()
          .resideOutsideOfPackage(CLI_MAPPER)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(DOMAIN_PATTERN)
          .allowEmptyShould(true);

  @ArchTest
  static final ArchRule only_cli_mappers_may_touch_domain =
      noClasses()
          .that()
          .resideInAnyPackage(CLI_ROOT)
          .and()
          .resideOutsideOfPackage(CLI_MAPPER)
          .should()
          .dependOnClassesThat(
              describe(
                  "domain types (CLI→domain mapping must live only in cli.mapper)",
                  (JavaClass c) ->
                      c.getPackageName() != null
                          && c.getPackageName().startsWith(BASE_PACKAGE + ".domain.")))
          .allowEmptyShould(true);
}
