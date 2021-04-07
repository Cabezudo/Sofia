package net.cabezudo.sofia.core.configuration;

import java.io.IOException;
import java.nio.file.Path;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.05
 */
class GlobalConfigurationFile {

  private static final String NAME = "global.configuration.json";

  private static final GlobalConfigurationFile INSTANCE = new GlobalConfigurationFile();

  private JSONObject configuration;

  public static GlobalConfigurationFile getInstance() {
    return INSTANCE;
  }

  private GlobalConfigurationFile() {
    // Protect the singleton instance
  }

  void load() throws JSONParseException {
    Path globalConfigurationFile = Configuration.getInstance().getSystemPath().resolve(NAME);
    try {
      configuration = JSON.parse(globalConfigurationFile, Configuration.getDefaultCharset()).toJSONObject();
    } catch (IOException e) {
      throw new SofiaRuntimeException("Can't open the global configuration file: " + globalConfigurationFile);
    }
  }

  public String get(String key) {
    String value = configuration.digNullString(key);
    if (value == null) {
      throw new SofiaRuntimeException("Key not found in global configuration: " + key);
    }
    return value;
  }

}
