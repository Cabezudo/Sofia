package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import net.cabezudo.sofia.core.configuration.Configuration;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.26
 */
public class Library {

  private final String name;
  private final String version;
  private final List<Path> javaScriptsFilePaths = new ArrayList<>();
  private final List<Path> styleSheetFilePaths = new ArrayList<>();
  private final Path libraryPath;

  Library(String reference) throws IOException {
    // TODO validate name format
    // TODO validate versión format
    // TODO add library using just the name.
    // TODO add > to version in order to accept that versión or greater
    // TODO add < to versión in order to accept that versión or less
    // TODO add x to version number in order to accept any minor or fix version
    int i = reference.indexOf(":");
    if (i != -1) {
      String[] pair = reference.split(":");
      this.name = pair[0];
      this.version = pair[1];
    } else {
      throw new RuntimeException("The library reference must have a colon separator: " + reference);
    }
    libraryPath = Configuration.getInstance().getCommonsLibsPath().resolve(name).resolve(version);
    loadJavaScriptPaths(libraryPath);
    loadStyleSheetPaths(libraryPath);
  }

  List<Path> getJavaScritpPaths() {
    return javaScriptsFilePaths;
  }

  List<Path> getStyleSheetFilePaths() {
    return styleSheetFilePaths;
  }

  private void loadJavaScriptPaths(Path libraryPath) throws IOException {
    loadPaths(libraryPath, ".js", javaScriptsFilePaths);
  }

  private void loadStyleSheetPaths(Path libraryPath) throws IOException {
    loadPaths(libraryPath, ".css", styleSheetFilePaths);
  }

  private void loadPaths(Path libraryPath, String extension, List<Path> filePaths) throws IOException {
    if (Files.isDirectory(libraryPath)) {
      Files.walkFileTree(libraryPath, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          if (file.toString().endsWith(extension)) {
            filePaths.add(file);
          }
          return FileVisitResult.CONTINUE;
        }
      });
    }
  }

  String getName() {
    return name;
  }

  String getVersion() {
    return version;
  }

  @Override
  public String toString() {
    return name + ":" + version;
  }
}
