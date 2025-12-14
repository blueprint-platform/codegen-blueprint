package io.github.blueprintplatform.codegen.adapter.out.shared.artifact;

import java.util.List;

public record ArtifactSpec(String basePath, List<TemplateSpec> templates) {}
