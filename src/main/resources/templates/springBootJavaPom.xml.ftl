<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>${pom.modelVersion}</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>${pom.projectMetadata.springBootVersion}</version>
        <relativePath/>
    </parent>

    <groupId>${pom.projectMetadata.groupId}</groupId>
    <artifactId>${pom.projectMetadata.artifactId}</artifactId>
    <version>${pom.version}</version>

    <properties>
        <java.version>${pom.projectMetadata.javaVersion}</java.version>
    </properties>

    <dependencies>
        <#list pom.dependencies as dependency>
            <dependency>
                <groupId>${dependency.groupId}</groupId>
                <artifactId>${dependency.artifactId}</artifactId>
                <#if dependency.version??>
                    <version>${dependency.version}</version>
                </#if>
                <#if dependency.scope??>
                    <scope>${dependency.scope}</scope>
                </#if>
            </dependency>
        </#list>
    </dependencies>

    <build>
        <plugins>
            <#list pom.plugins as plugin>
                <plugin>
                    <groupId>${plugin.groupId}</groupId>
                    <artifactId>${plugin.artifactId}</artifactId>
                    <#if plugin.version??>
                        <version>${plugin.version}</version>
                    </#if>
                    <#if plugin.configuration?has_content && plugin.configuration?size gt 0>
                        <configuration>
                        <#list plugin.configuration! {} as key, value>
                            <#if key != 'compileSourceRoots'>
                                <${key}>${value}</${key}>
                            </#if>
                        </#list>
                        <#if plugin.configuration.compileSourceRoots?has_content &&  plugin.configuration.compileSourceRoots?size gt 0>
                            <compileSourceRoots>
                                <#list plugin.configuration.compileSourceRoots! {} as sourceDirectory>
                                    <sourceDirectory>${sourceDirectory}</sourceDirectory>
                                </#list>
                            </compileSourceRoots>
                        </#if>
                        </configuration>
                    </#if>
                </plugin>
            </#list>
        </plugins>
    </build>

</project>