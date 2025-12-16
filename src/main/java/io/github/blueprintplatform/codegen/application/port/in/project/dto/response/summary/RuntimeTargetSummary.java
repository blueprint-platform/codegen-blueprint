package io.github.blueprintplatform.codegen.application.port.in.project.dto.response.summary;

import java.util.Map;

public record RuntimeTargetSummary(String type, Map<String, String> params) {}
