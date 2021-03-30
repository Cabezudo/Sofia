package net.cabezudo.sofia.core.money;

import java.math.BigDecimal;
import java.util.Currency;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.31
 */
public class Money {

  private final Currency currency;
  private final BigDecimal cost;

  public Money(Currency currency, int cost) {
    this.currency = currency;
    this.cost = new BigDecimal(cost);
  }

  public Money(Currency currency, String cost) {
    this.currency = currency;
    this.cost = new BigDecimal(cost);
  }

  public Money(Currency currency, BigDecimal cost) {
    this.currency = currency;
    this.cost = cost.setScale(2);
  }

  public Currency getCurrency() {
    return currency;
  }

  public BigDecimal getCost() {
    return cost;
  }

  public JSONObject toJSONTree() {
    JSONObject jsonRestaurantType = new JSONObject();
    jsonRestaurantType.add(new JSONPair("currency", currency.getCurrencyCode()));
    jsonRestaurantType.add(new JSONPair("cost", cost));
    return jsonRestaurantType;
  }

}
