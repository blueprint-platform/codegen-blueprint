package io.github.blueprintplatform.codegen.architecture.archunit;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = PackageCyclesArchitectureTest.BASE_PACKAGE,
    importOptions = ImportOption.DoNotIncludeTests.class)
class PackageCyclesArchitectureTest {

  static final String BASE_PACKAGE = "io.github.blueprintplatform.codegen";

  @ArchTest
  static final ArchRule top_level_packages_must_be_free_of_cycles =
      slices().matching(BASE_PACKAGE + ".(*)..").should().beFreeOfCycles().allowEmptyShould(true);

  @ArchTest
  static final ArchRule adapter_subpackages_must_be_free_of_cycles =
      slices()
          .matching(BASE_PACKAGE + ".adapter.(*)..")
          .should()
          .beFreeOfCycles()
          .allowEmptyShould(true);
}
