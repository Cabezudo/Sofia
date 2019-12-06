package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
public class CascadingStyleSheetSourcesContainer extends SofiaSourceContainer {

  private final CascadingStyleSheetImports imports;
  private final List<SofiaSource> files;

  CascadingStyleSheetSourcesContainer() {
    this.imports = new CascadingStyleSheetImports();
    this.files = new ArrayList<>();
  }

  CascadingStyleSheetSourcesContainer(Path fullPath) {
    this.imports = new CascadingStyleSheetImports();
    this.files = new ArrayList<>();
  }

  void load(Path basePath, String partialPathName, Caller caller) throws IOException, SiteCreationException, LibraryVersionException, SQLException {
    load(basePath, Paths.get(partialPathName), caller);
  }

  void load(Path basePath, Path partialPath, Caller caller) throws IOException, SiteCreationException, LibraryVersionException, SQLException {
    Path path = basePath.resolve(partialPath);
    SofiaSource file = new SofiaCascadingStyleSheetSource(caller);
    file.setPaths(basePath, partialPath);
    Logger.debug("Loadding %s file into container.", path);
    if (Files.exists(path)) {
      List<String> lines = Files.readAllLines(path);

      int lineNumber = 1;
      for (String s : lines) {
        if (!s.isEmpty()) {
          Line processedLine = processLine(partialPath, lineNumber, s);
          if (processedLine != null) {
            file.add(processedLine);
          }
        }
        lineNumber++;
      }
      files.add(file);
    } else {
      throw new RuntimeException("File not found: " + path);
    }
  }

  void load(SofiaSource source) {
    files.add(source);
  }

  private CodeLine processLine(Path path, int lineNumber, String s) {
    if (s.startsWith("@import url(")) {
      Line line = new CodeLine(s, lineNumber);
      imports.add(line);
      return null;
    }
    return new CodeLine(s, lineNumber);
  }

  @Override
  void save() throws SiteCreationException, IOException {
    Logger.debug("Saving file %s.", getTargetFilePath());
    StringBuilder code = new StringBuilder();
    for (Line line : imports) {
      code.append(line);
      code.append("\n");
    }
    for (SofiaSource sourceFile : files) {
      String description = sourceFile.getDescription();
      if (description != null) {
        code.append(description);
      }
      code.append(sourceFile.getCode());
      code.append("\n");
    }
    Files.write(getTargetFilePath(), code.toString().getBytes(Configuration.getInstance().getEncoding()));
  }

  void add(Libraries libraries) throws IOException {
    for (Library library : libraries) {
      List<SofiaCascadingStyleSheetSource> pathList = library.getCascadingStyleSheetSources();
      for (SofiaCascadingStyleSheetSource sofiaSource : pathList) {
        files.add(sofiaSource);
      }
    }
  }

  @Override
  protected void apply(TemplateVariables templateVariables) throws UndefinedLiteralException {
    for (Line line : imports) {
      super.apply(line, templateVariables);
    }
    for (SofiaSource file : files) {
      super.apply(file, templateVariables);
    }
  }

  void add(SofiaSource sofiaSource) {
    files.add(sofiaSource);
  }
}
