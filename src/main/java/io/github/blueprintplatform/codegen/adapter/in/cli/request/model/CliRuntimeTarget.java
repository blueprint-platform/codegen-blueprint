package io.github.blueprintplatform.codegen.adapter.in.cli.request.model;

import java.util.Map;

public record CliRuntimeTarget(String type, Map<String, String> params) {}
