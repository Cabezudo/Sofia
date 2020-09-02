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

  private final BigDecimal cost;
  private final Currency currency;

  public Money(int cost, Currency currency) {
    this.cost = new BigDecimal(cost);
    this.currency = currency;
  }

  public Money(String cost, Currency currency) {
    this.cost = new BigDecimal(cost);
    this.currency = currency;
  }

  public Money(BigDecimal cost, Currency currency) {
    this.cost = cost;
    this.currency = currency;
  }

  public BigDecimal getCost() {
    return cost;
  }

  public Currency getCurrency() {
    return currency;
  }

  public JSONObject toJSONTree() {
    JSONObject jsonRestaurantType = new JSONObject();
    jsonRestaurantType.add(new JSONPair("cost", cost));
    jsonRestaurantType.add(new JSONPair("currency", currency.getNumericCode()));
    return jsonRestaurantType;
  }

}
