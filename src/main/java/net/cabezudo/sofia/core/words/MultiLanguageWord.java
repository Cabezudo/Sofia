package net.cabezudo.sofia.core.words;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.languages.InvalidTwoLettersCodeException;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.languages.LanguageManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.03.25
 */
public class MultiLanguageWord {

  private final Map<Language, String> map = new TreeMap<>();

  public MultiLanguageWord(JSONObject jsonObject) throws JSONParseException, ClusterException {
    List<String> keys = jsonObject.getKeyList();
    for (String twoLettersCode : keys) {
      Language language;
      try {
        language = LanguageManager.getInstance().get(twoLettersCode);
      } catch (InvalidTwoLettersCodeException e) {
        throw new JSONParseException("Invalid language code: " + twoLettersCode, jsonObject.getPosition());
      }
      String text;
      try {
        text = jsonObject.getString(twoLettersCode);
      } catch (PropertyNotExistException e) {
        throw new SofiaRuntimeException(e);
      }
      map.put(language, text);
    }
  }

  public JSONObject toJSONTree() {
    JSONObject jsonObject = new JSONObject();
    map.entrySet().forEach(entry -> jsonObject.add(new JSONPair(entry.getKey().getTwoLetterCode(), entry.getValue())));
    return jsonObject;
  }

}
