package io.github.blueprintplatform.codegen.archunit;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "io.github.blueprintplatform.codegen",
    importOptions = ImportOption.DoNotIncludeTests.class)
class PackageCyclesArchitectureTest {

  @ArchTest
  static final ArchRule layers_must_be_free_of_cycles =
      slices().matching("io.github.blueprintplatform.codegen.(*)..").should().beFreeOfCycles();

  @ArchTest
  static final ArchRule adapter_subpackages_must_be_free_of_cycles =
      slices()
          .matching("io.github.blueprintplatform.codegen.adapter.(*)..")
          .should()
          .beFreeOfCycles();
}
