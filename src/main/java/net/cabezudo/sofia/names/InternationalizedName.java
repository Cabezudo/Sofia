package net.cabezudo.sofia.names;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.languages.Language;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.21
 */
public class InternationalizedName {

  private final int id;
  private final Language language;
  private final String value;

  public InternationalizedName(InternationalizedName name) {
    this.id = name.getId();
    this.language = name.getLanguage();
    this.value = name.getValue();
  }

  public InternationalizedName(int id, Language language, String value) {
    this.id = id;
    this.language = language;
    this.value = value;
  }

  public int getId() {
    return id;
  }

  public Language getLanguage() {
    return language;
  }

  public String getValue() {
    return value;
  }

  public JSONObject toJSONTree() {
    JSONObject jsonInternationalizedName = new JSONObject();
    jsonInternationalizedName.add(new JSONPair("id", id));
    jsonInternationalizedName.add(new JSONPair("language", language.toJSONTree()));
    jsonInternationalizedName.add(new JSONPair("value", value));
    return jsonInternationalizedName;
  }

}
