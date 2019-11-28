package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
  private final List<SofiaJavaScriptSource> javaScriptsFilePaths = new ArrayList<>();
  private final List<SofiaCascadingStyleSheetSource> styleSheetFilePaths = new ArrayList<>();

  Library(String reference, Caller caller) throws IOException {
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
    Path basePath = Configuration.getInstance().getCommonsLibsPath();
    Path partialPath = Paths.get(name).resolve(version);
    loadJavaScriptPaths(basePath, partialPath, caller);
    loadStyleSheetPaths(basePath, partialPath, caller);
  }

  List<SofiaJavaScriptSource> getJavaScritpPaths() {
    return javaScriptsFilePaths;
  }

  List<SofiaCascadingStyleSheetSource> getCascadingStyleSheetSources() {
    return styleSheetFilePaths;
  }

  private void loadJavaScriptPaths(Path basePath, Path partialPath, Caller caller) throws IOException {
    Path libraryPath = basePath.resolve(partialPath);
    if (Files.isDirectory(libraryPath)) {
      Files.walkFileTree(libraryPath, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
          if (filePath.toString().endsWith(".js")) {
            Path basePath = Configuration.getInstance().getCommonSourcesPath();
            SofiaJavaScriptSource sofiaSource = new SofiaJavaScriptSource(caller);
            Path partialPath = basePath.relativize(filePath);
            sofiaSource.setPaths(basePath, partialPath);
            sofiaSource.load();
            javaScriptsFilePaths.add(sofiaSource);
          }
          return FileVisitResult.CONTINUE;
        }
      });
    }
  }

  private void loadStyleSheetPaths(Path basePath, Path partialPath, Caller caller) throws IOException {
    Path libraryPath = basePath.resolve(partialPath);
    if (Files.isDirectory(libraryPath)) {
      Files.walkFileTree(libraryPath, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
          if (filePath.toString().endsWith(".css")) {
            Path basePath = Configuration.getInstance().getCommonSourcesPath();
            SofiaCascadingStyleSheetSource sofiaSource = new SofiaCascadingStyleSheetSource(caller);
            Path partialPath = basePath.relativize(filePath);
            sofiaSource.setPaths(basePath, partialPath);
            sofiaSource.load();
            styleSheetFilePaths.add(sofiaSource);
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
