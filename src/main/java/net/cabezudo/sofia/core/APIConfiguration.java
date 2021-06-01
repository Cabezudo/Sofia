package net.cabezudo.sofia.core;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.configuration.ConfigurationException;
import net.cabezudo.sofia.core.configuration.DataCreator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.10.19
 */
public class APIConfiguration {

  private final Map<String, APIEntries> map = new TreeMap<>();

  public void add(Path apiConfigurationFilePath) throws ConfigurationException {
    try {
      JSONObject jsonObject = JSON.parse(apiConfigurationFilePath, Configuration.getDefaultCharset()).toJSONObject();
      add(jsonObject, apiConfigurationFilePath);
    } catch (JSONParseException | IOException e) {
      throw new ConfigurationException(e);
    }
  }

  public void add(JSONObject jsonObject, Path apiConfigurationFilePath) throws ConfigurationException {
    List<String> keys = jsonObject.getKeyList();
    for (String key : keys) {
      APIEntries apiEntries = map.get(key);
      if (apiEntries == null) {
        apiEntries = new APIEntries();
        map.put(key, apiEntries);
      }
      for (JSONValue jsonValue : jsonObject.getNullJSONArray(key)) {
        JSONObject jsonEntry = jsonValue.toJSONObject();
        JSONValue jsonClassName = jsonEntry.getNullValue("class");
        if (jsonClassName == null) {
          continue;
        }
        String className = jsonClassName.toString();
        if (className.isBlank()) {
          continue;
        }
        try {
          Class.forName(className);
        } catch (ClassNotFoundException e) {
          net.cabezudo.json.Position position = jsonClassName.getPosition();
          PositionFile positionFile = new PositionFile(apiConfigurationFilePath, position.getLine(), position.getRow());
          throw new ConfigurationException("Class not found: " + className + ":" + positionFile);
        }
        APIEntry apiEntry = new APIEntry(jsonEntry.getNullString("path"), className);
        apiEntries.add(apiEntry);

      }
    }
  }

  public void add(DataCreator dataCreator, Path path) throws ConfigurationException {
    JSONObject apiConfiguration = dataCreator.getAPIConfiguration();
    add(apiConfiguration, path);
  }

  public APIEntries get(String key) {
    return map.get(key);
  }
}
