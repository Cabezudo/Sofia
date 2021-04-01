package net.cabezudo.sofia.core.languages;

import java.util.Objects;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.11
 */
public class Language implements Comparable<Language> {

  public static String ENGLISH = "en";

  private final int id;
  private final String twoLetterCode;

  public Language(int id, String twoLetterCode) {
    this.id = id;
    this.twoLetterCode = twoLetterCode;
  }

  public int getId() {
    return id;
  }

  public String getTwoLetterCode() {
    return twoLetterCode;
  }

  @Override
  public String toString() {
    return "[ " + id + ", " + twoLetterCode + " ]";
  }

  public JSONObject toJSONTree() {
    JSONObject jsonRestaurantType = new JSONObject();
    jsonRestaurantType.add(new JSONPair("id", id));
    jsonRestaurantType.add(new JSONPair("twoLetterCode", twoLetterCode));
    return jsonRestaurantType;
  }

  @Override
  public int compareTo(Language l) {
    return this.getTwoLetterCode().compareTo(l.getTwoLetterCode());
  }

  @Override
  public int hashCode() {
    return getTwoLetterCode().hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    if (getClass() != o.getClass()) {
      return false;
    }
    final Language l = (Language) o;
    return Objects.equals(this.getTwoLetterCode(), l.getTwoLetterCode());
  }
}
