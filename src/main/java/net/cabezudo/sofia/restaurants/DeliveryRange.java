package net.cabezudo.sofia.restaurants;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.02
 */
public class DeliveryRange {

  private final int min;
  private final int max;

  public DeliveryRange(int min, int max) {
    this.min = min;
    this.max = max;
  }

  public DeliveryRange(int value) {
    this.min = value;
    this.max = value;
  }

  int getMin() {
    return min;
  }

  int getMax() {
    return max;
  }

  JSONObject toJSONTree() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("min", min));
    jsonObject.add(new JSONPair("max", max));
    return jsonObject;

  }

}
