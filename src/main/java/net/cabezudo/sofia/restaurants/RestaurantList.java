package net.cabezudo.sofia.restaurants;

import java.sql.SQLException;
import java.util.Iterator;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.EntityList;
import net.cabezudo.sofia.core.money.Money;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.29
 */
public class RestaurantList extends EntityList<Restaurant> {

  Restaurants restaurants = new Restaurants();

  public RestaurantList(int offset, int pageSize) {
    super(offset, pageSize);
  }

  public RestaurantList(int offset) {
    this(offset, 50);
  }

  @Override
  public Iterator<Restaurant> iterator() {
    return restaurants.iterator();
  }

  public void add(Restaurant restaurant) throws SQLException {
    restaurants.add(restaurant);
  }

  @Override
  public String toJSON() {
    return toJSONTree().toString();
  }

  @Override
  public JSONValue toJSONTree() {
    JSONObject listObject = new JSONObject();
    JSONArray jsonRecords = new JSONArray();
    JSONPair jsonRecordsPair = new JSONPair("records", jsonRecords);
    listObject.add(jsonRecordsPair);
    restaurants.forEach((restaurant) -> {
      JSONObject jsonRestaurant = new JSONObject();
      jsonRestaurant.add(new JSONPair("id", restaurant.getId()));
      jsonRestaurant.add(new JSONPair("subdomain", restaurant.getSubdomain()));
      jsonRestaurant.add(new JSONPair("imageName", restaurant.getImageName()));
      jsonRestaurant.add(new JSONPair("name", restaurant.getName()));
      JSONObject jsonShippingCost = new JSONObject();
      String typeName;
      if (restaurant.getType() == null) {
        typeName = null;
      } else {
        typeName = restaurant.getType().getName();
      }
      jsonRestaurant.add(new JSONPair("type", typeName));
      jsonRestaurant.add(new JSONPair("priceRange", restaurant.getPriceRange()));
      Money shippingCost = restaurant.getShippingCost();
      jsonShippingCost.add(new JSONPair("cost", shippingCost.getCost()));
      jsonShippingCost.add(new JSONPair("currency", shippingCost.getCurrency().getCurrencyCode()));
      jsonRestaurant.add(new JSONPair("shippingCost", jsonShippingCost));
      jsonRestaurant.add(new JSONPair("minDeliveryTime", restaurant.getMinDeliveryTime()));
      jsonRestaurant.add(new JSONPair("maxDeliveryTime", restaurant.getMaxDeliveryTime()));
      jsonRecords.add(jsonRestaurant);
    });

    return listObject;

  }

}
