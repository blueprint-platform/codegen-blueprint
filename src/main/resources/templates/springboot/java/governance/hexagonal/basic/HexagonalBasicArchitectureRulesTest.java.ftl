package ${projectPackageName}.architecture.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.BASE_PACKAGE;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_ADAPTER;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_APPLICATION;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_BOOTSTRAP;
import static ${projectPackageName}.architecture.archunit.HexagonalGuardrailsScope.FAMILY_DOMAIN;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import java.util.TreeSet;

/** Basic Hexagonal dependency directions and context-root cycle checks. */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalBasicArchitectureRulesTest {

    <#include "../_application-direction.ftl">
    @ArchTest
    static final ArchRule domain_must_not_depend_on_outer_families =
            noClasses().that().resideInAnyPackage(familyPattern(FAMILY_DOMAIN))
                    .should().dependOnClassesThat().resideInAnyPackage(
                            familyPattern(FAMILY_APPLICATION),
                            familyPattern(FAMILY_ADAPTER),
                            familyPattern(FAMILY_BOOTSTRAP))
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule outside_packages_must_not_depend_on_bootstrap =
            noClasses()
                    .that()
                    .resideOutsideOfPackage(familyPattern(FAMILY_BOOTSTRAP))
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(familyPattern(FAMILY_BOOTSTRAP))
                    .allowEmptyShould(true);

    <#assign schemaTest = "HexagonalPackageSchemaSanityTest">
    <#include "../../_context-cycles.ftl">

    private static String familyPattern(String family) {
        return BASE_PACKAGE + ".." + family + "..";
    }
}