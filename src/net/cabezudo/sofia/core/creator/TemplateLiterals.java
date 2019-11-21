package net.cabezudo.sofia.core.creator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.30
 */
public class TemplateLiterals {

  private final JSONObject jsonObject;

  public TemplateLiterals() {
    jsonObject = new JSONObject();
  }

  public void add(Path basePath, String fileName) throws FileNotFoundException, IOException, JSONParseException, UndefinedLiteralException {
    Path partialFilePath = Paths.get(fileName);
    Path fullPath = basePath.resolve(fileName);
    if (Files.exists(fullPath)) {
      Logger.debug("Template literals file FOUND: %s", fullPath);
      List<String> lines = Files.readAllLines(fullPath);
      StringBuilder sb = new StringBuilder();
      int lineNumber = 1;
      for (String line : lines) {
        sb.append(replace(line, lineNumber, partialFilePath));
        lineNumber++;
      }
      JSONObject newJSONObject = new JSONObject(sb.toString());
      jsonObject.merge(newJSONObject);
    } else {
      Logger.debug("Template literals file NOT FOUND: %s", fullPath);
    }
  }

  String replace(String l, int lineNumber, Path partialFilePath) throws UndefinedLiteralException {
    StringBuilder sb = new StringBuilder();
    int i;
    int last = 0;

    String line = l.trim();
    while ((i = line.indexOf("#{", last)) != -1) {
      sb.append(line.substring(last, i));
      last = line.indexOf("}", i);
      String name = line.substring(i + 2, last);
      last++;
      String value;
      try {
        value = digString(name);
      } catch (PropertyNotExistException e) {
        Position position = new Position(lineNumber, i + 3);
        throw new UndefinedLiteralException(e.getPropertyName(), partialFilePath, position, e);
      }
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

  void add(JSONObject jsonData) {
    jsonObject.merge(jsonData);
  }

  public String get(String themeName) {
    return jsonObject.getNullString("themeName");
  }

  String digString(String name) throws PropertyNotExistException {
    return jsonObject.digString(name);
  }
}
