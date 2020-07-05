package net.cabezudo.sofia.core.sic.objects.values;

import java.math.BigDecimal;
import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICNumberOrAspect extends SICValue<BigDecimal> {

  private final BigDecimal value;
  private boolean isNumber;
  private boolean isDecimal;
  private boolean isAspect;

  public SICNumberOrAspect(Token token) throws SICCompileTimeException {
    super(token);
    try {
      String sValue = getToken().getValue();
      int i = sValue.indexOf(":");
      if (i != -1) {
        i = sValue.indexOf(":", i + 1);
        if (i != -1) {
          throw new SICCompileTimeException("Invalid aspect format or parameter value.", getToken());
        }
        String values[] = sValue.split(":");
        try {
          BigDecimal a = new BigDecimal(values[0]);
          BigDecimal b = new BigDecimal(values[1]);
          value = a.divide(b);
          isAspect = true;
        } catch (NumberFormatException e) {
          throw new SICCompileTimeException("Invalid aspect format.", e, getToken());
        }
      } else {
        value = new BigDecimal(sValue);
        isNumber = true;
      }
      isDecimal = !value.remainder(BigDecimal.ONE).equals(BigDecimal.ZERO);
    } catch (NumberFormatException e) {
      throw new SICCompileTimeException("Invalid parameter value.", getToken());
    }
  }

  @Override
  public final boolean isAspect() {
    return isAspect;
  }

  @Override
  public final boolean isDecimal() {
    return isDecimal;
  }

  @Override
  public final boolean isNumber() {
    return isNumber;
  }

  public boolean isZero() {
    return getValue().intValue() == 0;
  }

  @Override
  public String getTypeName() {
    return "numberOrAspect";
  }

  @Override
  public BigDecimal getValue() {
    return value;
  }
}
