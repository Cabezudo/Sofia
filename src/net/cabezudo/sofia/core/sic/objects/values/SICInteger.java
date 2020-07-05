package net.cabezudo.sofia.core.sic.objects.values;

import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICInteger extends SICNumber<Integer> {

  private final Integer value;

  public SICInteger(Token token) throws SICCompileTimeException {
    super(token);
    try {
      value = Integer.parseInt(getToken().getValue());
    } catch (NumberFormatException e) {
      throw new SICCompileTimeException("Invalid parameter value.", getToken());
    }
  }

  @Override
  public String getTypeName() {
    return "integer";
  }

  @Override
  public boolean isInteger() {
    return true;
  }

  @Override
  public boolean isZero() {
    return getValue() == 0;
  }

  @Override
  public Integer getValue() {
    return value;
  }

}
