package ${projectPackageName}.architecture.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.BASE_PACKAGE;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.DOMAIN_SERVICE;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_CONTROLLER;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_DOMAIN;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_REPOSITORY;
import static ${projectPackageName}.architecture.archunit.StandardGuardrailsScope.FAMILY_SERVICE;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import java.util.TreeSet;

/** Basic Standard dependency directions and context-root cycle checks. */
@AnalyzeClasses(
        packages = BASE_PACKAGE,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class StandardBasicArchitectureRulesTest {

    @ArchTest
    static final ArchRule controllers_must_not_depend_on_repositories =
            noClasses()
                    .that()
                    .resideInAnyPackage(familyPattern(FAMILY_CONTROLLER))
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(familyPattern(FAMILY_REPOSITORY))
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule services_must_not_depend_on_controllers =
            noClasses().that().resideInAnyPackage(familyPattern(FAMILY_SERVICE))
                    .should().dependOnClassesThat().resideInAnyPackage(familyPattern(FAMILY_CONTROLLER))
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule repositories_must_not_depend_on_controllers =
            noClasses()
                    .that()
                    .resideInAnyPackage(familyPattern(FAMILY_REPOSITORY))
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(familyPattern(FAMILY_CONTROLLER))
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule controllers_must_not_depend_on_domain_services =
            noClasses()
                    .that()
                    .resideInAnyPackage(familyPattern(FAMILY_CONTROLLER))
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(domainServicePattern())
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule domain_must_not_depend_on_controller_service_or_repository_layers =
            noClasses()
                    .that()
                    .resideInAnyPackage(familyPattern(FAMILY_DOMAIN))
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            familyPattern(FAMILY_CONTROLLER),
                            familyPattern(FAMILY_SERVICE),
                            familyPattern(FAMILY_REPOSITORY)
                    )
                    .allowEmptyShould(true);

    <#assign schemaTest = "StandardPackageSchemaSanityTest">
    <#include "../../_context-cycles.ftl">

    private static String domainServicePattern() {
        return BASE_PACKAGE + ".." + FAMILY_DOMAIN + "." + DOMAIN_SERVICE + "..";
    }

    private static String familyPattern(String family) {
        return BASE_PACKAGE + ".." + family + "..";
    }
}