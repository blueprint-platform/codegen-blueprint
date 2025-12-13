package io.github.blueprintplatform.codegen.application.usecase.project;

import io.github.blueprintplatform.codegen.application.usecase.project.model.CreateProjectResult;
import io.github.blueprintplatform.codegen.application.usecase.project.model.GeneratedFileSummary;
import io.github.blueprintplatform.codegen.application.usecase.project.model.ProjectDependencySummary;
import io.github.blueprintplatform.codegen.application.usecase.project.model.ProjectGenerationSummary;
import io.github.blueprintplatform.codegen.domain.model.ProjectBlueprint;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependencies;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependency;
import io.github.blueprintplatform.codegen.domain.model.value.dependency.DependencyCoordinates;
import io.github.blueprintplatform.codegen.domain.model.value.identity.ArtifactId;
import io.github.blueprintplatform.codegen.domain.model.value.identity.GroupId;
import io.github.blueprintplatform.codegen.domain.model.value.identity.ProjectIdentity;
import io.github.blueprintplatform.codegen.domain.model.value.layout.ProjectLayout;
import io.github.blueprintplatform.codegen.domain.model.value.naming.ProjectDescription;
import io.github.blueprintplatform.codegen.domain.model.value.naming.ProjectName;
import io.github.blueprintplatform.codegen.domain.model.value.pkg.PackageName;
import io.github.blueprintplatform.codegen.domain.model.value.sample.SampleCodeOptions;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.PlatformTarget;
import io.github.blueprintplatform.codegen.domain.model.value.tech.stack.TechStack;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CreateProjectResultMapper {

    private static final Set<String> EXECUTABLE_FILE_NAMES =
            Set.of("mvnw", "gradlew");

    private static final Set<String> EXECUTABLE_EXTENSIONS =
            Set.of(".sh", ".cmd", ".bat");

    private static final Set<String> BINARY_EXTENSIONS =
            Set.of(".jar", ".zip", ".png", ".jpg", ".jpeg", ".gif", ".pdf", ".ico");

    public CreateProjectResult from(
            ProjectBlueprint blueprint,
            Path projectRoot,
            List<Path> projectFiles,
            Path archivePath) {

        ProjectGenerationSummary projectSummary = toProjectGenerationSummary(blueprint);

        List<GeneratedFileSummary> files =
                projectFiles == null
                        ? List.of()
                        : projectFiles.stream()
                        .map(path -> toGeneratedFileSummary(projectRoot, path))
                        .toList();

        return new CreateProjectResult(projectSummary, projectRoot, archivePath, files);
    }

    private GeneratedFileSummary toGeneratedFileSummary(Path projectRoot, Path relativePath) {
        Path absolutePath = projectRoot.resolve(relativePath);

        boolean executable = isExecutableFile(absolutePath, relativePath);
        boolean binary = isBinaryFile(relativePath);

        return new GeneratedFileSummary(relativePath, binary, executable);
    }

    private ProjectGenerationSummary toProjectGenerationSummary(ProjectBlueprint blueprint) {
        ProjectIdentity identity = blueprint.getIdentity();

        GroupId groupId = identity.groupId();
        ArtifactId artifactId = identity.artifactId();
        ProjectName name = blueprint.getName();
        ProjectDescription description = blueprint.getDescription();
        PackageName packageName = blueprint.getPackageName();
        TechStack techStack = blueprint.getTechStack();
        ProjectLayout layout = blueprint.getLayout();
        PlatformTarget platformTarget = blueprint.getPlatformTarget();
        SampleCodeOptions sampleCode = blueprint.getSampleCodeOptions();
        Dependencies dependencies = blueprint.getDependencies();

        List<ProjectDependencySummary> dependencySummaries =
                dependencies.asList().stream()
                        .map(this::toProjectDependencySummary)
                        .toList();

        return new ProjectGenerationSummary(
                groupId.value(),
                artifactId.value(),
                name.value(),
                description.value(),
                packageName.value(),
                techStack,
                layout,
                platformTarget,
                sampleCode,
                dependencySummaries);
    }

    private ProjectDependencySummary toProjectDependencySummary(Dependency dependency) {
        DependencyCoordinates coordinates = dependency.coordinates();

        String version =
                dependency.version() != null ? dependency.version().value() : null;

        String scope =
                dependency.scope() != null
                        ? dependency.scope().name().toLowerCase(Locale.ROOT)
                        : null;

        return new ProjectDependencySummary(
                coordinates.groupId().value(),
                coordinates.artifactId().value(),
                version,
                scope);
    }

    private boolean isExecutableFile(Path absolutePath, Path relativePath) {
        if (Files.isExecutable(absolutePath)) {
            return true;
        }
        return hasExecutableName(relativePath) || hasExecutableExtension(relativePath);
    }

    private boolean isBinaryFile(Path relativePath) {
        String name = fileNameLower(relativePath);
        return BINARY_EXTENSIONS.stream().anyMatch(name::endsWith);
    }

    private boolean hasExecutableName(Path relativePath) {
        return EXECUTABLE_FILE_NAMES.contains(fileNameLower(relativePath));
    }

    private boolean hasExecutableExtension(Path relativePath) {
        String name = fileNameLower(relativePath);
        return EXECUTABLE_EXTENSIONS.stream().anyMatch(name::endsWith);
    }

    private String fileNameLower(Path relativePath) {
        Path fileName = relativePath.getFileName();
        String raw = fileName != null ? fileName.toString() : relativePath.toString();
        return raw.toLowerCase(Locale.ROOT);
    }
}