package net.cabezudo.sofia.core.creator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

  public void add(Path basePath, String fileName) throws FileNotFoundException, IOException, JSONParseException {
    Path fullPath = basePath.resolve(fileName);
    if (Files.exists(fullPath)) {
      Logger.debug("Template literals file FOUND: %s", fullPath);
      List<String> lines = Files.readAllLines(fullPath);
      StringBuilder sb = new StringBuilder();
      for (String line : lines) {
        sb.append(line.trim());
      }
      JSONObject newJSONObject = new JSONObject(sb.toString());
      jsonObject.merge(newJSONObject);
    } else {
      Logger.debug("Template literals file NOT FOUND: %s", fullPath);
    }
  }

  public String toJSON() {
    return jsonObject.toJSON();
  }

  @Override
  public String toString() {
    return this.toJSON();
  }

  String apply(String code) throws UndefinedLiteralException {
    StringBuilder sb = new StringBuilder(code.length() * 2);
    int last = 0;
    int i;
    // TODO Leer línea por línea para pode indicar el número de línea y de columna del error.
    while ((i = code.indexOf("#{", last)) != -1) {
      sb.append(code.substring(last, i));
      last = code.indexOf("}", i);
      String name = code.substring(i + 2, last);
      last++;
      String value;
      try {
        value = jsonObject.digString(name);
      } catch (PropertyNotExistException e) {
        throw new UndefinedLiteralException("The property " + name + " doesn't exist in any json template literal.", e, 0, 0);
      }
      sb.append(value);
    }
    sb.append(code.substring(last));
    return sb.toString();
  }

  void add(JSONObject jsonData) {
    jsonObject.merge(jsonData);
  }
}
