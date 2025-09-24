package io.github.bsayli.codegen.initializr.projectgeneration.model;

import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.BuildTool;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.Framework;
import io.github.bsayli.codegen.initializr.domain.model.value.tech.stack.Language;

public record ProjectType(Framework framework, BuildTool buildTool, Language language) {}
