package net.cabezudo.sofia.core.creator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.30
 */
public class TemplateVariables {

  private final JSONObject jsonObject;
  private final Site site;
  private final String partialVoidPathName;

  public TemplateVariables(Site site, String partialVoidPathName) {
    jsonObject = new JSONObject();
    this.site = site;
    this.partialVoidPathName = partialVoidPathName;
  }

  public void add(Path basePath, String fileName) throws FileNotFoundException, IOException, JSONParseException, UndefinedLiteralException {
    add(basePath, fileName, null);
  }

  public void add(Path basePath, String fileName, String id) throws FileNotFoundException, IOException, JSONParseException, UndefinedLiteralException {
    Path partialFilePath = Paths.get(fileName);
    Path fullPath = basePath.resolve(fileName);
    Logger.debug("Search template literals file: %s", fullPath);
    if (Files.exists(fullPath)) {
      Logger.debug("Template literals file FOUND: %s", fullPath);
      List<String> lines = Files.readAllLines(fullPath);
      StringBuilder sb = new StringBuilder();
      int lineNumber = 1;
      for (String line : lines) {
        sb.append(replace(line, lineNumber, partialFilePath));
        lineNumber++;
      }
      if (id != null) {
        JSONObject newJSONObject = new JSONObject(sb.toString());
        JSONObject idObject = new JSONObject();
        JSONPair idPair = new JSONPair(id, newJSONObject);
        idObject.add(idPair);
        jsonObject.merge(idObject);
      } else {
        JSONObject newJSONObject = new JSONObject(sb.toString());
        jsonObject.merge(newJSONObject);
      }
    } else {
      Logger.debug("Template literals file to add to template variables NOT FOUND: %s", fullPath);
    }
  }

  String replace(String line, int lineNumber, Path partialFilePath) throws UndefinedLiteralException {
    return replace(null, line, lineNumber, partialFilePath);
  }

  String replace(String id, String line, int lineNumber, Path partialFilePath) throws UndefinedLiteralException {
    StringBuilder sb = new StringBuilder();
    int i;
    int last = 0;

    while ((i = line.indexOf("#{", last)) != -1) {
      sb.append(line.substring(last, i));
      last = line.indexOf("}", i);
      String name;
      if (id == null) {
        name = line.substring(i + 2, last);
      } else {
        name = id + "." + line.substring(i + 2, last);
      }
      last++;
      String value;
      try {
        value = digString(name);
      } catch (PropertyNotExistException e) {
        throw new UndefinedLiteralException(name, i + 3, e);
      }
      Logger.debug("Replace %s with %s.", name, value);
      sb.append(value);
    }
    sb.append(line.substring(last));
    return sb.toString();
  }

  public String toJSON() {
    return jsonObject.toJSON();
  }

  @Override
  public String toString() {
    return this.toJSON();
  }

  void merge(JSONObject jsonData) {
    jsonObject.merge(jsonData);
  }

  public String get(String themeName) {
    return jsonObject.getNullString("themeName");
  }

  String digString(String name) throws PropertyNotExistException {
    return jsonObject.digString(name);
  }
}
