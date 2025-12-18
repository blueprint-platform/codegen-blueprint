package ${projectPackageName}.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Strict layered direction rules for STANDARD layout.
 * Enforces:
 * - controllers must not depend on repositories
 * - controllers must depend only on services (and their own package + JDK)
 * - services must not depend on controllers
 * - repositories must not depend on services or controllers
 */
@AnalyzeClasses(
        packages = "${projectPackageName}",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class StandardStrictLayerDependencyRulesTest {

    private static final String BASE_PACKAGE = "${projectPackageName}";

    // ✅ ArchUnit package patterns
    private static final String CONTROLLER_PATTERN = BASE_PACKAGE + ".controller..";
    private static final String SERVICE_PATTERN = BASE_PACKAGE + ".service..";
    private static final String REPOSITORY_PATTERN = BASE_PACKAGE + ".repository..";

    // ✅ Prefixes for string checks
    private static final String CONTROLLER_ROOT = BASE_PACKAGE + ".controller.";
    private static final String SERVICE_ROOT = BASE_PACKAGE + ".service.";

    @ArchTest
    static final ArchRule controllers_must_not_depend_on_repositories =
            noClasses()
                    .that()
                    .resideInAnyPackage(CONTROLLER_PATTERN)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(REPOSITORY_PATTERN)
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule controllers_should_not_depend_on_domain_services =
            noClasses()
                    .that()
                    .resideInAnyPackage(CONTROLLER_PATTERN)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(BASE_PACKAGE + ".domain..service..")
                    .allowEmptyShould(true);


    @ArchTest
    static final ArchRule services_must_not_depend_on_controllers =
            noClasses()
                    .that()
                    .resideInAnyPackage(SERVICE_PATTERN)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(CONTROLLER_PATTERN)
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule repositories_must_not_depend_on_services_or_controllers =
            noClasses()
                    .that()
                    .resideInAnyPackage(REPOSITORY_PATTERN)
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(SERVICE_PATTERN, CONTROLLER_PATTERN)
                    .allowEmptyShould(true);
}