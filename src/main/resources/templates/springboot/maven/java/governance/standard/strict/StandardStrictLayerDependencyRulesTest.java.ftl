package ${projectPackageName}.architecture;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

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
    static final ArchRule controllers_must_depend_only_on_services_and_controller_types_and_jdk =
            noClasses()
                    .that()
                    .resideInAnyPackage(CONTROLLER_PATTERN)
                    .should()
                    .dependOnClassesThat(
                            describe(
                                    "be outside controller/service packages and not be JDK types",
                                    (JavaClass c) -> !isAllowedControllerDependency(c)
                            )
                    )
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

    private static boolean isAllowedControllerDependency(JavaClass c) {
        String pkg = c.getPackageName();

        if (pkg == null || pkg.isBlank()) {
            return true;
        }

        if (pkg.startsWith("java.") || pkg.startsWith("javax.")) {
            return true;
        }

        if (pkg.startsWith(CONTROLLER_ROOT)) {
            return true;
        }

        return pkg.startsWith(SERVICE_ROOT);
    }
}