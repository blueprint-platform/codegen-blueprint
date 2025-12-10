package io.github.blueprintplatform.codegen.domain.port.out.artifact;

import static io.github.blueprintplatform.codegen.domain.policy.file.GeneratedFilePolicy.*;

import java.nio.file.Path;
import java.util.Arrays;

public record GeneratedBinaryResource(Path relativePath, byte[] bytes)
    implements GeneratedResource {

  public GeneratedBinaryResource(Path relativePath, byte[] bytes) {
    requireRelativePath(relativePath);
    requireBinaryContent(bytes);
    this.relativePath = relativePath;
    this.bytes = Arrays.copyOf(bytes, bytes.length);
  }

  @Override
  public byte[] bytes() {
    return Arrays.copyOf(bytes, bytes.length);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof GeneratedBinaryResource(Path path, byte[] bytes1))) {
      return false;
    }
    return relativePath.equals(path) && Arrays.equals(bytes, bytes1);
  }

  @Override
  public int hashCode() {
    int result = relativePath.hashCode();
    result = 31 * result + Arrays.hashCode(bytes);
    return result;
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public String toString() {
    return "GeneratedBinaryFile[" + relativePath + ", size=" + bytes.length + "]";
  }
}
