package net.cabezudo.sofia.core.sic.objects.values;

import java.math.BigDecimal;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICPercentage extends SICDecimal {

  public SICPercentage(BigDecimal bd) {
    super(bd);
  }

  @Override
  public String getTypeName() {
    return "percentaje";
  }

  @Override
  public String toString() {
    return getValue() + "%";
  }

  @Override
  public boolean isPercentage() {
    return true;
  }
}