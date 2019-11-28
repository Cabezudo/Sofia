package net.cabezudo.sofia.core.creator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.26
 */
class ThemeSourceFile {

  private List<CascadingStyleSheetSourceFile> files = new ArrayList<>();
  private String themeName;

  ThemeSourceFile(String themeName, TemplateVariables templateVariables) throws IOException, SiteCreationException {
    this.themeName = themeName;

    Path themeBasePath = Configuration.getInstance().getCommonsThemesPath().resolve(themeName);
    try {
      templateVariables.add(themeBasePath, "values.json");
    } catch (UndefinedLiteralException | JSONParseException | FileNotFoundException e) {
      throw new SiteCreationException(e.getMessage());
    }
    CascadingStyleSheetSourceFile file = new CascadingStyleSheetSourceFile(themeBasePath, "style.css", templateVariables, null);
    if (Files.exists(file.getFullTargetFilePath())) {
      List<String> fileLines = Files.readAllLines(file.getFullTargetFilePath());

      int lineNumber = 1;
      for (String s : fileLines) {
        if (!s.isEmpty()) {
          Line line = new CodeLine(s, lineNumber);
          file.add(line);
        }
        lineNumber++;
      }
      files.add(file);
    } else {
      Logger.warning("File NOT FOUND: %s.", file.getFullTargetFilePath());
    }
  }

  String getName() {
    return themeName;
  }

  List<CascadingStyleSheetSourceFile> getFiles() {
    return files;
  }
}
