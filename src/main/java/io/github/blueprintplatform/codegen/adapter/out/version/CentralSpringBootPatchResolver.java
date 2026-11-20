package io.github.blueprintplatform.codegen.adapter.out.version;

import io.github.blueprintplatform.codegen.adapter.error.exception.version.SpringBootResolutionException;
import io.github.blueprintplatform.codegen.application.port.out.SpringBootPatchResolver;
import io.github.blueprintplatform.codegen.domain.model.value.tech.platform.SpringBootVersion;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public final class CentralSpringBootPatchResolver implements SpringBootPatchResolver {
  private static final String GROUP = "org.springframework.boot";
  private static final String ARTIFACT = "spring-boot-starter-parent";
  private final CentralRepositoryHttp repository;

  public CentralSpringBootPatchResolver() {
    this(
        URI.create(
            "https://repo.maven.apache.org/maven2/org/springframework/boot/" + ARTIFACT + "/"));
  }

  CentralSpringBootPatchResolver(URI base) {
    this.repository = new CentralRepositoryHttp(base);
  }

  @Override
  public String resolveLatest(SpringBootVersion line) {
    long deadline = System.nanoTime() + Duration.ofSeconds(8).toNanos();
    Element metadata = read("metadata", "maven-metadata.xml", 1024 * 1024, deadline);
    require(
        "metadata".equals(metadata.getTagName())
            && GROUP.equals(value(metadata, "groupId"))
            && ARTIFACT.equals(value(metadata, "artifactId")),
        "Unexpected Central metadata coordinates");
    Element versions = child(child(metadata, "versioning"), "versions");
    var candidates = new ArrayList<String>();
    for (Node node = versions == null ? null : versions.getFirstChild();
        node != null;
        node = node.getNextSibling()) {
      if (node instanceof Element version && "version".equals(version.getTagName())) {
        String patch = version.getTextContent().strip();
        if (line.matchesPatch(patch)) candidates.add(patch);
      }
    }
    String patch =
        candidates.stream()
            .max(
                Comparator.comparingInt(
                    candidate -> Integer.parseInt(candidate.substring(line.key().length() + 1))))
            .orElseThrow(
                () ->
                    new SpringBootResolutionException(
                        "No stable Central Spring Boot patch for " + line));
    Element pom =
        read("parent POM", patch + "/" + ARTIFACT + "-" + patch + ".pom", 512 * 1024, deadline);
    Element parent = child(pom, "parent");
    require(
        "project".equals(pom.getTagName())
            && ARTIFACT.equals(value(pom, "artifactId"))
            && GROUP.equals(inherited(pom, parent, "groupId"))
            && patch.equals(inherited(pom, parent, "version")),
        "Unexpected Central parent POM coordinates");
    return patch;
  }

  private Element read(String phase, String path, int limit, long deadline) {
    byte[] body = repository.read(phase, path, limit, deadline);
    try {
      var factory = DocumentBuilderFactory.newInstance();
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
      var parser = factory.newDocumentBuilder();
      parser.setErrorHandler(
          new DefaultHandler() {
            @Override
            public void fatalError(SAXParseException e) throws SAXException {
              throw e;
            }
          });
      return parser.parse(new ByteArrayInputStream(body)).getDocumentElement();
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new SpringBootResolutionException("Central " + phase + " failed: " + e.getMessage(), e);
    }
  }

  private static Element child(Element parent, String name) {
    if (parent == null) return null;
    for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node instanceof Element element && name.equals(element.getTagName())) return element;
    }
    return null;
  }

  private static String value(Element parent, String name) {
    Element element = child(parent, name);
    return element == null ? null : element.getTextContent().strip();
  }

  private static String inherited(Element pom, Element parent, String name) {
    String direct = value(pom, name);
    return direct == null ? value(parent, name) : direct;
  }

  private static void require(boolean valid, String message) {
    if (!valid) throw new SpringBootResolutionException(message);
  }
}
