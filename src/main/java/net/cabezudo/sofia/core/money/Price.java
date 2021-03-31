package net.cabezudo.sofia.core.money;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.currency.Currency;
import net.cabezudo.sofia.core.currency.CurrencyManager;
import net.cabezudo.sofia.core.currency.InvalidCurrencyCodeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.03.25
 */
public class Price {

  Map<String, Money> map = new TreeMap<>();

  public Price(JSONObject jsonPrice) throws PropertyNotExistException, ClusterException, JSONParseException {
    for (String key : jsonPrice.getKeyList()) {
      Currency currency;
      try {
        currency = CurrencyManager.getInstance().get(key);
      } catch (InvalidCurrencyCodeException e) {
        throw new JSONParseException(e.getMessage(), jsonPrice.getPosition());
      }
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

  public Money get(Currency currency) {
    return map.get(currency.getCurrencyCode());
  }
}
