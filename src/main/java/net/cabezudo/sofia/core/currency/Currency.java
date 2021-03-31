package net.cabezudo.sofia.core.currency;

import java.util.Objects;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.11
 */
public class Currency implements Comparable<Currency> {

  private final int id;
  private final String currencyCode;

  public Currency(int id, String currencyCode) {
    this.id = id;
    this.currencyCode = currencyCode;
  }

  public int getId() {
    return id;
  }

  public String getCurrencyCode() {
    return currencyCode;
  }

  @Override
  public String toString() {
    return "[ " + id + ", " + currencyCode + " ]";
  }

  public JSONObject toJSONTree() {
    JSONObject jsonRestaurantType = new JSONObject();
    jsonRestaurantType.add(new JSONPair("id", id));
    jsonRestaurantType.add(new JSONPair("currencyCode", currencyCode));
    return jsonRestaurantType;
  }

  @Override
  public int compareTo(Currency l) {
    return this.getCurrencyCode().compareTo(l.getCurrencyCode());
  }

  @Override
  public int hashCode() {
    return getCurrencyCode().hashCode();
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
    final Currency l = (Currency) o;
    return Objects.equals(this.getCurrencyCode(), l.getCurrencyCode());
  }
}
