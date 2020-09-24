package net.cabezudo.sofia.food;

import net.cabezudo.json.values.JSONArray;
import net.cabezudo.sofia.restaurants.OpenTimes;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.23
 */
public class CategoryHours {

  private final OpenTimes cleanTimes;

  public CategoryHours(OpenTimes cleanTimes) {
    this.cleanTimes = cleanTimes;
  }

  public JSONArray toJSONTree() {
    return cleanTimes.toJSONTree();
  }
}
