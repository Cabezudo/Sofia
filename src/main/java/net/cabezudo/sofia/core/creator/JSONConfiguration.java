package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import net.cabezudo.json.JSON;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.07
 */
class JSONConfiguration {

  private Path jsonPartialPath;
  private JSONObject jsonObject;
  private final StringBuilder sb = new StringBuilder();

  JSONObject load(HTMLSourceFile htmlSourceFile)
          throws IOException, LocatedSiteCreationException, SiteCreationException, InvalidFragmentTag, LibraryVersionConflictException, JSONParseException {
    jsonPartialPath = Paths.get(htmlSourceFile.getVoidPartialPathName() + ".json");
    Path jsonSourceFilePath = htmlSourceFile.getBasePath().resolve(jsonPartialPath);
    Logger.debug("Search configuration file %s for HTML source file %s.", jsonPartialPath, htmlSourceFile.getPartialFilePath());
    if (Files.isRegularFile(jsonSourceFilePath)) {
      Logger.debug("FOUND configuration file %s for HTML %s.", jsonPartialPath, htmlSourceFile.getPartialFilePath());

      // Read the JSON file into a buffer and apply template variables before parse.
      List<String> jsonLines = Files.readAllLines(jsonSourceFilePath);

      int lineNumber = 1;
      for (String line : jsonLines) {
        try {
          sb.append(htmlSourceFile.getTemplateVariables().replace(line));
          sb.append('\n');
        } catch (UndefinedLiteralException e) {
          Position position = new Position(lineNumber, e.getRow());
          if (Environment.getInstance().isDevelopment()) {
            Logger.finest("%s", htmlSourceFile.getTemplateVariables());
          }
          throw new LocatedSiteCreationException(e.getMessage(), jsonPartialPath, position);
        }
        lineNumber++;
      }

      // Parse the configuration file
      try {
        jsonObject = JSON.parse(sb.toString()).toJSONObject();
      } catch (JSONParseException e) {
        throw new SiteCreationException("Can't parse " + jsonSourceFilePath + ". " + e.getMessage());
      }

      if (htmlSourceFile.getId() != null) {
        JSONPair configurationPair = new JSONPair(htmlSourceFile.getId(), getJSONObject());
        JSONObject jsonConfigurationWithId = new JSONObject();
        jsonConfigurationWithId.add(configurationPair);
        htmlSourceFile.getTemplateVariables().merge(jsonConfigurationWithId);
      } else {
        htmlSourceFile.getTemplateVariables().merge(getJSONObject());
      }

    }
    return jsonObject;
  }

  JSONObject getJSONObject() {
    return jsonObject;
  }

  Path getJSONPartialPath() {
    return jsonPartialPath;
  }

  String getOriginalContent() {
    return sb.toString();
  }

}
