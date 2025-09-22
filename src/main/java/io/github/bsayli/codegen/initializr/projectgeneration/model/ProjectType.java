package io.github.bsayli.codegen.initializr.projectgeneration.model;

import io.github.bsayli.codegen.initializr.domain.model.value.tech.options.BuildTool;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.options.Framework;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.options.Language;

public record ProjectType(Framework framework, BuildTool buildTool, Language language) {}
