package net.cabezudo.sofia.food;

import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.restaurants.OpenTime;
import net.cabezudo.sofia.restaurants.OpenTimes;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.23
 */
public class CategoryHours {

  private final OpenTime time;

  public CategoryHours(OpenTimes cleanTimes) {
    if (cleanTimes.size() > 1) {
      // Yes is arbitrary but is more dificult to show more than one and don't have any sense to have more than one.
      throw new SofiaRuntimeException("A category can't have more than one time.");
    }
    if (cleanTimes.isEmpty()) {
      time = null;
    } else {
      time = cleanTimes.get(0);
    }
  }

  public JSONObject toJSONTree() {
    if (time == null) {
      return null;
    }
    return time.toJSONTree();
  }
}
