package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.02.19
 */
public abstract class SofiaSourceContainer {

  protected final TemplateLiterals templateLiterals;

  public enum Type {
    HTML("html"), CSS("css"), JS("js");

    private final String extension;

    Type(String extension) {
      this.extension = extension;
    }

    private String getExtension() {
      return extension;
    }
  }

  protected final StringBuilder sb;

  SofiaSourceContainer(String code, TemplateLiterals templateLiterals) {
    sb = new StringBuilder(code);
    this.templateLiterals = templateLiterals;
  }

  SofiaSourceContainer(TemplateLiterals templateLiterals) {
    this("", templateLiterals);
  }

  protected abstract void save(Site site, String fileName) throws IOException, SiteCreationException;

  public StringBuilder append(char c) {
    return sb.append(c);
  }

  public StringBuilder append(String code) {
    return sb.append(code);
  }

  public String getCode() {
    return sb.toString();
  }

  public abstract Type getType();

  void append(Path path) throws IOException {
    Logger.debug("Load %s file", path);
    if (Files.isRegularFile(path)) {
      if (Files.exists(path)) {
        processLine("/* File: " + Configuration.getInstance().getSitesDataPath().relativize(path) + " */");
        try (Stream<String> stream = Files.lines(path)) {
          stream.forEach(this::processLine);
        }
      }
      return;
    }

    if (Files.isDirectory(path)) {
      Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          if (file.toString().endsWith("." + getType().getExtension())) {
            Logger.debug("Loading the file %s.", file);
            append(file); // Recursivity
          }
          return FileVisitResult.CONTINUE;
        }

      });
    }
  }

  protected void processLine(String line) {
    append(line);
    append("\n");
  }
}
