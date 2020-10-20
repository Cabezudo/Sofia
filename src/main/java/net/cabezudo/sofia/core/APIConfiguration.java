package net.cabezudo.sofia.core;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
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
      JSONObject jsonObject = JSON.parse(apiConfigurationFilePath, Configuration.getDefaultCharset().toString()).toJSONObject();
      add(jsonObject);
    } catch (JSONParseException | IOException e) {
      throw new ConfigurationException(e);
    }
  }

  public void add(JSONObject jsonObject) {
    List<String> keys = jsonObject.getKeyList();
    for (String key : keys) {
      APIEntries apiEntries = map.get(key);
      if (apiEntries == null) {
        apiEntries = new APIEntries();
        map.put(key, apiEntries);
      }
      apiEntries.add(jsonObject.getNullJSONArray(key));
    }
  }

  public void add(DataCreator dataCreator) {
    JSONObject apiConfiguration = dataCreator.getAPIConfiguration();
    add(apiConfiguration);
  }

  public APIEntries get(String key) {
    return map.get(key);
  }
}
