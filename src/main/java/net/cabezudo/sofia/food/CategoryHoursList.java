package net.cabezudo.sofia.food;

import java.util.Map;
import java.util.Map.Entry;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
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

  public JSONArray toJSONTree() {
    JSONArray jsonArray = new JSONArray();
    for (Entry<Category, CategoryHours> entry : hoursByCategoryMap.entrySet()) {
      JSONObject jsonCategoryHours = new JSONObject();
      Category category = entry.getKey();
      JSONObject jsonCategory = new JSONObject();
      jsonCategory.add(new JSONPair("id", category.getId()));
      jsonCategory.add(new JSONPair("name", category.getName()));
      jsonCategoryHours.add(new JSONPair("category", jsonCategory));
      jsonCategoryHours.add(new JSONPair("hours", entry.getValue().toJSONTree()));
      jsonArray.add(jsonCategoryHours);
    }
    return jsonArray;
  }
}
