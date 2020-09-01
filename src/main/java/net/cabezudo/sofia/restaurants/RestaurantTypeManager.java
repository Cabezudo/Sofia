package net.cabezudo.sofia.restaurants;

import java.sql.SQLException;
import net.cabezudo.sofia.core.catalogs.SimpleCatalogManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.31
 */
public class RestaurantTypeManager extends SimpleCatalogManager<RestaurantType> {

  private static final RestaurantTypeManager INSTANCE = new RestaurantTypeManager();

  private RestaurantTypeManager() {
    super(RestaurantTypesTable.DATABASE, RestaurantTypesTable.NAME);
  }

  public static RestaurantTypeManager getInstance() {
    return INSTANCE;
  }

  @Override
  public RestaurantType get(int id) throws SQLException {
    return new RestaurantType(super.get(id));
  }

  @Override
  public RestaurantType get(String name) throws SQLException {
    return new RestaurantType(super.get(name));
  }
}
