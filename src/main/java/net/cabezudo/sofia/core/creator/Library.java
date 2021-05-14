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
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.26
 */
public class Library {

  private final String name;
  private final String version;
  private final List<JSSourceFile> jsSourceFiles = new ArrayList<>();
  private final List<CSSSourceFile> cssSourceFiles = new ArrayList<>();
  private final List<TextsFile> textsFiles = new ArrayList<>();
  private final Caller caller;

  Library(Site site, String reference, TemplateVariables templateVariables, Caller caller) throws IOException, LocatedSiteCreationException {
    // TODO validate name format
    // TODO validate versión format
    // TODO add library using just the name.
    // TODO add > to version in order to accept that versión or greater
    // TODO add < to versión in order to accept that versión or less
    // TODO add x to version number in order to accept any minor or fix version
    int i = reference.lastIndexOf("/");
    if (i != -1) {
      this.name = reference.substring(0, i);
      this.version = reference.substring(i + 1);
    } else {
      throw new SofiaRuntimeException("The library reference must have a slash to separate name and version: " + reference + " called from " + caller);
    }
    this.caller = caller;
    Path basePath = Configuration.getInstance().getCommonsLibsPath();
    Path partialPath = Paths.get(name).resolve(version);
    loadFiles(site, basePath, partialPath, templateVariables, caller);
  }

  Caller getCaller() {
    return caller;
  }

  List<JSSourceFile> getJavaScritpFiles() {
    return jsSourceFiles;
  }

  List<CSSSourceFile> getCascadingStyleSheetFiles() {
    return cssSourceFiles;
  }

  List<TextsFile> getTextsFiles() {
    return textsFiles;
  }

  private void loadFiles(Site site, Path basePath, Path partialPath, TemplateVariables templateVariables, Caller caller) throws IOException, LocatedSiteCreationException {
    Path libraryPath = basePath.resolve(partialPath);
    if (Files.isDirectory(libraryPath)) {
      List<Path> files = new ArrayList<>();
      Files.walkFileTree(libraryPath, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
          files.add(filePath);
          return FileVisitResult.CONTINUE;
        }
      });
      for (Path filePath : files) {
        Path partialFilePath = basePath.relativize(filePath);
        if (filePath.toString().endsWith(".js")) {
          Logger.debug("JS file library %s FOUND.", filePath);
          JSSourceFile file = new JSSourceFile(site, basePath, partialFilePath, templateVariables, caller);
          file.loadFile();

          jsSourceFiles.add(file);
        }
        if (filePath.toString().endsWith(".css")) {
          Logger.debug("CSS file library %s FOUND.", filePath);
          CSSSourceFile file = new CSSSourceFile(site, basePath, partialFilePath, templateVariables, caller);
          file.load(basePath, partialFilePath.toString(), caller);
          cssSourceFiles.add(file);
        }
        if (filePath.toString().endsWith("texts.json")) {
          Logger.debug("Texts file %s FOUND.", filePath);
          TextsFile file = new TextsFile();
          file.load(filePath);
          textsFiles.add(file);
        }
      }
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
    return name + "/" + version;
  }
}
