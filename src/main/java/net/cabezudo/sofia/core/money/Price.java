package net.cabezudo.sofia.core.money;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.TreeMap;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.03.25
 */
public class Price {

  Map<String, Money> map = new TreeMap<>();

  public Price(JSONObject jsonPrice) throws PropertyNotExistException {
    for (String key : jsonPrice.getKeyList()) {
      Currency currency = Currency.getInstance(key);
      BigDecimal price = jsonPrice.getBigDecimal(key);
      Money money = new Money(currency, price);
      map.put(key, money);
    }
  }

  public JSONObject toJSONTree() {
    JSONObject jsonObject = new JSONObject();
    map.entrySet().forEach(entry -> {
      BigDecimal cost = entry.getValue().getCost();
      cost = cost.setScale(2);
      jsonObject.add(new JSONPair(entry.getKey(), cost));
    });
    return jsonObject;
  }
}
