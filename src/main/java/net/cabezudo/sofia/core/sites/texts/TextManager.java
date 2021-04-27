package net.cabezudo.sofia.core.sites.texts;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.regex.Pattern;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.13
 */
public class TextManager {

  private static final TextManager INSTANCE = new TextManager();

  private TextManager() {
    jsonTexts = new JSONObject();
  }

  public static TextManager getInstance() {
    return INSTANCE;
  }

  public static JSONObject get(Site site, String absolutPage, Language language) throws JSONParseException, IOException {
    String page = absolutPage.substring(1);
    Path path = site.getVersionPath().resolve(page + ".texts").resolve(language.getTwoLetterCode() + ".json");
    Logger.debug("Read the language file %s.", path);
    try {
      return JSON.parse(path, Configuration.getDefaultCharset()).toJSONObject();
    } catch (NoSuchFileException e) {
      Logger.debug(e.getMessage());
      return new JSONObject();
    }
  }

  public static void add(JSONObject jsonTexts) {
    INSTANCE.jsonTexts.merge(jsonTexts);
  }

  private final JSONObject jsonTexts;

  public static String get(Language language, String messageKey, Object... parameters) {
    JSONObject jsonTexts = INSTANCE.jsonTexts.getNullObject(messageKey);
    if (jsonTexts == null) {
      throw new InvalidKeyException("I can't found the text key " + messageKey);
    }
    String text = jsonTexts.getNullString(language.getTwoLetterCode());

    // TODO Make this more efficient.
    int i = 0;
    for (Object object : parameters) {
      text = text.replaceAll(Pattern.quote("{" + i + "}"), object.toString());
      i++;
    }
    return text;
  }

  public void loadTexts() throws JSONParseException, IOException {
    Path textsFilePath = Configuration.getInstance().getSystemResourcesPath().resolve("texts.json");
    JSONObject fileJSONTexts = JSON.parse(textsFilePath, Configuration.getDefaultCharset()).toJSONObject();
    TextManager.add(fileJSONTexts);
  }

}
