package io.github.blueprintplatform.codegen.adapter.in.cli.request;

import io.github.blueprintplatform.codegen.adapter.in.cli.request.model.CliArchitectureSpec;
import io.github.blueprintplatform.codegen.adapter.in.cli.request.model.CliDependency;
import io.github.blueprintplatform.codegen.adapter.in.cli.request.model.CliProjectMetadata;
import io.github.blueprintplatform.codegen.adapter.in.cli.request.model.CliRuntimeTarget;
import io.github.blueprintplatform.codegen.adapter.in.cli.request.model.CliTechStack;
import java.nio.file.Path;
import java.util.List;

public record CliProjectRequest(
    CliProjectMetadata metadata,
    CliTechStack techStack,
    CliRuntimeTarget runtimeTarget,
    CliArchitectureSpec architecture,
    List<CliDependency> dependencies,
    Path targetDirectory) {}
