package com.c9.codegen.initializr.projectgeneration.model;

import com.c9.codegen.initializr.projectgeneration.model.techstack.BuildTool;
import com.c9.codegen.initializr.projectgeneration.model.techstack.Framework;
import com.c9.codegen.initializr.projectgeneration.model.techstack.Language;

public record ProjectType(Framework framework, BuildTool buildTool, Language language) {}
