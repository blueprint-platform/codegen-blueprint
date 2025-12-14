package io.github.blueprintplatform.codegen.bootstrap.wiring.out.profile;

import io.github.blueprintplatform.codegen.adapter.out.shared.SampleCodeLayoutSpec;
import io.github.blueprintplatform.codegen.bootstrap.config.SampleCodeProperties;
import org.springframework.stereotype.Component;

@Component
public class SampleCodeSpecMapper {

  public SampleCodeLayoutSpec from(SampleCodeProperties p) {
    return new SampleCodeLayoutSpec(toRoots(p.roots()), toLevels(p.levels()));
  }

  private SampleCodeLayoutSpec.Roots toRoots(SampleCodeProperties.Roots roots) {
    return new SampleCodeLayoutSpec.Roots(roots.standard(), roots.hexagonal());
  }

  private SampleCodeLayoutSpec.Levels toLevels(SampleCodeProperties.Levels levels) {
    return new SampleCodeLayoutSpec.Levels(levels.basicDirName(), levels.richDirName());
  }
}
