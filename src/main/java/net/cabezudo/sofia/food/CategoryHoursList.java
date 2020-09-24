package net.cabezudo.sofia.food;

import java.util.Map;
import java.util.Map.Entry;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.schedule.BusinessHours;
import net.cabezudo.sofia.restaurants.Restaurant;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.22
 */
public class CategoryHoursList {

  private final BusinessHours businessHours;
  private final Map<Category, CategoryHours> hoursByCategoryMap;

  public CategoryHoursList(Restaurant restaurant, Categories categories) {
    businessHours = restaurant.getBusinessHours();
    hoursByCategoryMap = businessHours.getHoursById(categories);
  }

  public JSONObject toJSONTree() {
    JSONObject jsonObject = new JSONObject();
    for (Entry<Category, CategoryHours> entry : hoursByCategoryMap.entrySet()) {
      Category category = entry.getKey();
      jsonObject.add(new JSONPair(category.getName(), entry.getValue().toJSONTree()));
    }
    return jsonObject;
  }
}
