package com.c9.codegen.initializr.projectgeneration.model.maven;

import java.util.LinkedHashMap;
import java.util.Map;

public class MavenPlugin {

  private String groupId;
  private String artifactId;
  private String version;
  private Map<String, Object> configuration = new LinkedHashMap<>();

  private MavenPlugin(MavenPluginBuilder builder) {
    this.groupId = builder.groupId;
    this.artifactId = builder.artifactId;
    this.version = builder.version;
    this.configuration = builder.configuration;
  }

  public String getGroupId() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public String getVersion() {
    return version;
  }

  public Map<String, Object> getConfiguration() {
    return configuration;
  }

  public static class MavenPluginBuilder {
    private String groupId;
    private String artifactId;
    private String version;
    private Map<String, Object> configuration = new LinkedHashMap<>();

    public MavenPluginBuilder groupId(String groupId) {
      this.groupId = groupId;
      return this;
    }

    public MavenPluginBuilder artifactId(String artifactId) {
      this.artifactId = artifactId;
      return this;
    }

    public MavenPluginBuilder version(String version) {
      this.version = version;
      return this;
    }

    public MavenPluginBuilder addConfigurationElement(String key, Object value) {
      configuration.put(key, value);
      return this;
    }

    public MavenPlugin build() {
      if (groupId == null || artifactId == null) {
        throw new IllegalStateException("groupId, artifactId are required fields");
      }
      return new MavenPlugin(this);
    }
  }
}
