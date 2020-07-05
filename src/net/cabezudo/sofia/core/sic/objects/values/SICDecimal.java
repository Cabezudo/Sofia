package net.cabezudo.sofia.core.sic.objects.values;

import java.math.BigDecimal;
import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICDecimal extends SICNumber<BigDecimal> {

  private final BigDecimal value;
  private boolean isDecimal;

  public SICDecimal(Token token) throws SICCompileTimeException {
    super(token);
    try {
      value = new BigDecimal(getToken().getValue());
      isDecimal = !value.remainder(BigDecimal.ONE).equals(BigDecimal.ZERO);
    } catch (NumberFormatException e) {
      throw new SICCompileTimeException("Invalid value.", e, getToken());
    }
  }

  @Override
  public String getTypeName() {
    return "decimal";
  }

  @Override
  public boolean isDecimal() {
    return isDecimal;
  }

  @Override
  public boolean isZero() {
    return getValue().intValue() == 0;
  }

  @Override
  public BigDecimal getValue() {
    return value;
  }
}
