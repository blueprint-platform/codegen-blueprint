package io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java.build;

import io.github.blueprintplatform.codegen.adapter.out.build.maven.shared.PomDependency;
import io.github.blueprintplatform.codegen.adapter.out.shared.dependency.DependencyFeature;

final class MavenPomBuildModel {

    private MavenPomBuildModel() {}

    static final String KEY_GROUP_ID = "groupId";
    static final String KEY_ARTIFACT_ID = "artifactId";
    static final String KEY_JAVA_VERSION = "javaVersion";
    static final String KEY_SPRING_BOOT_VER = "springBootVersion";
    static final String KEY_DEPENDENCIES = "dependencies";
    static final String KEY_PROJECT_NAME = "projectName";
    static final String KEY_PROJECT_DESCRIPTION = "projectDescription";
    static final String KEY_POM_PROPERTIES = "pomProperties";

    static final String ARCH_UNIT_VERSION_KEY = "archunit.version";
    static final String ARCH_UNIT_VERSION = "1.4.1";

    private static final String SPRING_BOOT_GROUP_ID = "org.springframework.boot";

    static final DependencyFeature JPA_STARTER =
            new DependencyFeature("jpa", SPRING_BOOT_GROUP_ID, "spring-boot-starter-data-jpa");

    static final PomDependency CORE_STARTER =
            PomDependency.of(SPRING_BOOT_GROUP_ID, "spring-boot-starter");

    static final PomDependency TEST_STARTER =
            PomDependency.of(SPRING_BOOT_GROUP_ID, "spring-boot-starter-test", null, "test");

    static final PomDependency H2_DB = PomDependency.of("com.h2database", "h2", null, null);

    static final PomDependency ARCH_UNIT_TEST =
            PomDependency.ofWithVersionProperty(
                    "com.tngtech.archunit", "archunit-junit5", ARCH_UNIT_VERSION_KEY, "test");
}