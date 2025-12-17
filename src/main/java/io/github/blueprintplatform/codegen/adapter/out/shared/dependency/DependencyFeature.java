package io.github.blueprintplatform.codegen.adapter.out.shared.dependency;

import io.github.blueprintplatform.codegen.domain.model.value.dependency.Dependency;
import java.util.Objects;
import java.util.function.Predicate;

public record DependencyFeature(
        String key,
        String groupId,
        String artifactId
) {

    public Predicate<Dependency> matches() {
        return d ->
                Objects.equals(groupId, d.coordinates().groupId().value())
                        && Objects.equals(artifactId, d.coordinates().artifactId().value());
    }
}