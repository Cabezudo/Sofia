package net.cabezudo.sofia.core.sic.objects.values;

import java.math.BigDecimal;
import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICNumberOrPercentage extends SICValue<BigDecimal> {

  private final BigDecimal value;
  private boolean isNumber = false;
  private boolean isPercentage = false;
  private boolean isDecimal;

  public SICNumberOrPercentage(Token token) throws SICCompileTimeException {
    super(token);
    try {
      String sValue = getToken().getValue();
      if (sValue.endsWith("%")) {
        sValue = sValue.substring(0, sValue.length() - 1);
        isPercentage = true;
      } else {
        isNumber = true;
      }
      value = new BigDecimal(sValue);
      isDecimal = !value.remainder(BigDecimal.ONE).equals(BigDecimal.ZERO);
    } catch (NumberFormatException e) {
      throw new SICCompileTimeException("Invalid parameter value.", getToken());
    }
  }

  @Override
  public final boolean isDecimal() {
    return isDecimal;
  }

  @Override
  public final boolean isNumber() {
    return isNumber;
  }

  @Override
  public final boolean isPercentage() {
    return isPercentage;
  }

  public boolean isZero() {
    return getValue().intValue() == 0;
  }

  @Override
  public String getTypeName() {
    return "numberOrPercentage";
  }

  @Override
  public BigDecimal getValue() {
    return value;
  }
}
