package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.cabezudo.json.JSON;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.03.19
 */
class TextsFile {

  private final JSONObject jsonTexts = new JSONObject();

  private JSONObject getJSONTree() {
    return jsonTexts;
  }

  void load(Path filePath) throws IOException {
    try {
      JSONObject jsonTextsFromFile = JSON.parse(filePath, Configuration.getDefaultCharset()).toJSONObject();
      jsonTexts.merge(jsonTextsFromFile);
    } catch (JSONParseException e) {
      throw new SofiaRuntimeException(e);
    }
  }

  public void add(JSONObject jsonTexts) {
    this.jsonTexts.replace(jsonTexts);
  }

  void add(Libraries libraries) {
    for (Library library : libraries) {
      List<TextsFile> textsFiles = library.getTextsFiles();
      for (TextsFile textsFile : textsFiles) {
        jsonTexts.merge(textsFile.getJSONTree());
      }
    }
  }

  void save(Path textsPartialPath) throws IOException {
    Path fullTextsDirectoryPath = Paths.get(textsPartialPath.toString() + ".texts");
    if (!Files.exists(fullTextsDirectoryPath)) {
      Files.createDirectory(fullTextsDirectoryPath);
    }

    List<String> keys = jsonTexts.getKeyList();
    Set<String> languageSet = new TreeSet<>();
    for (String key : keys) {
      JSONObject jsonLanguages = jsonTexts.getNullObject(key);
      List<String> languages = jsonLanguages.getKeyList();
      for (String language : languages) {
        languageSet.add(language);
      }
    }
    for (String language : languageSet) {
      JSONObject jsonTextObject = new JSONObject();
      for (String key : keys) {
        JSONObject allLanguages = jsonTexts.getNullObject(key);
        String text = allLanguages.getNullString(language);
        if (text == null) {
          text = allLanguages.getNullString("en");
          if (text == null) {
            if (allLanguages.size() > 0) {
              text = allLanguages.getNullString(0);
            }
            if (text == null) {
              text = key;
            }
          }
        }
        jsonTextObject.add(new JSONPair(key, text));
      }
      Path languageFilePath = fullTextsDirectoryPath.resolve(language + ".json");
      Logger.debug("Save language file %s", languageFilePath);
      Files.write(languageFilePath, jsonTextObject.toJSON().getBytes(Configuration.getInstance().getEncoding()));
    }
  }
}
