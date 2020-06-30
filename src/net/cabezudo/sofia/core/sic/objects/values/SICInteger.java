package net.cabezudo.sofia.core.sic.objects.values;

import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICInteger extends SICNumber<Integer> {

  public SICInteger(Token token, Integer value) {
    super(token, value);
  }

  @Override
  public String getTypeName() {
    return "number";
  }

  @Override
  public boolean isInteger() {
    return true;
  }

  @Override
  public boolean isZero() {
    return getValue() == 0;
  }
}
