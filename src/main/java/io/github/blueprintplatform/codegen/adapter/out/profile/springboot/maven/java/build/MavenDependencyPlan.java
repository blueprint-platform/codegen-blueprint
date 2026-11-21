package io.github.blueprintplatform.codegen.adapter.out.profile.springboot.maven.java.build;

import io.github.blueprintplatform.codegen.adapter.out.build.shared.BuildDependency;
import io.github.blueprintplatform.codegen.adapter.out.build.shared.BuildDependencyMapper;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import java.util.ArrayList;
import java.util.List;

public final class MavenDependencyPlan {
  static final BuildDependency H2_DB = BuildDependency.of("com.h2database", "h2", null, null);
  static final BuildDependency ARCH_UNIT_TEST =
      BuildDependency.ofWithVersionProperty(
          "com.tngtech.archunit",
          "archunit-junit6",
          MavenPomBuildModel.ARCH_UNIT_VERSION_KEY,
          "test");
  private static final String SPRING_BOOT_GROUP_ID = "org.springframework.boot";
  private static final String JPA_STARTER = "spring-boot-starter-data-jpa";

  static final BuildDependency CORE_STARTER =
      BuildDependency.of(SPRING_BOOT_GROUP_ID, "spring-boot-starter");

  static final BuildDependency TEST_STARTER =
      BuildDependency.of(SPRING_BOOT_GROUP_ID, "spring-boot-starter-test", null, "test");

  static final BuildDependency WEB_STARTER =
      BuildDependency.of(SPRING_BOOT_GROUP_ID, "spring-boot-starter-webmvc");

  private final List<BuildDependency> dependencies;
  private final boolean mvcPresent;
  private final boolean sampleEnabled;

  private MavenDependencyPlan(
      List<BuildDependency> dependencies, boolean mvcPresent, boolean sampleEnabled) {
    this.dependencies = List.copyOf(dependencies);
    this.mvcPresent = mvcPresent;
    this.sampleEnabled = sampleEnabled;
  }

  public static MavenDependencyPlan from(ProjectBlueprint blueprint, BuildDependencyMapper mapper) {
    var selected = mapper.from(blueprint.getDependencies());
    var dependencies = new ArrayList<BuildDependency>();
    dependencies.add(CORE_STARTER);
    dependencies.addAll(selected);

    boolean sample = blueprint.getArchitecture().sampleCodeOptions().isEnabled();
    boolean explicitMvc = includesBootStarter(selected, WEB_STARTER.artifactId());
    if (sample && !explicitMvc) {
      dependencies.add(WEB_STARTER);
    }
    if (includesBootStarter(selected, JPA_STARTER)) {
      dependencies.add(H2_DB);
    }
    if (blueprint.getArchitecture().governance().isEnabled()) {
      dependencies.add(ARCH_UNIT_TEST);
    }
    dependencies.add(TEST_STARTER);
    if (sample) {
      dependencies.add(
          BuildDependency.of(
              SPRING_BOOT_GROUP_ID, "spring-boot-starter-webmvc-test", null, "test"));
      if (includesBootStarter(selected, "spring-boot-starter-security")) {
        dependencies.add(
            BuildDependency.of(
                SPRING_BOOT_GROUP_ID, "spring-boot-starter-security-test", null, "test"));
      }
    }
    return new MavenDependencyPlan(dependencies, explicitMvc || sample, sample);
  }

  private static boolean includesBootStarter(List<BuildDependency> selected, String artifactId) {
    return selected.stream()
        .anyMatch(
            dependency ->
                SPRING_BOOT_GROUP_ID.equals(dependency.groupId())
                    && artifactId.equals(dependency.artifactId()));
  }

  public List<BuildDependency> dependencies() {
    return dependencies;
  }

  public boolean mvcPresent() {
    return mvcPresent;
  }

  public boolean sampleEnabled() {
    return sampleEnabled;
  }
}
