package net.cabezudo.sofia.core.sic.objects;

import java.math.BigDecimal;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.sic.objects.values.SICDecimal;
import net.cabezudo.sofia.core.sic.objects.values.SICInteger;
import net.cabezudo.sofia.core.sic.objects.values.SICPercentage;
import net.cabezudo.sofia.core.sic.objects.values.SICString;
import net.cabezudo.sofia.core.sic.objects.values.SICValue;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class ValueFactory {

  public static SICValue<?> get(Token valueToken) {
    String value = valueToken.getValue();
    if (value.endsWith("%")) {
      value = Utils.chop(value, 1);
      try {
        BigDecimal bd = new BigDecimal(value);
        return new SICPercentage(bd);
      } catch (NumberFormatException e) {
        return new SICString(value);
      }
    }

    try {
      Integer integer = Integer.parseInt(value);
      return new SICInteger(integer);
    } catch (NumberFormatException e) {
      // We can't parse an integer. Continue trying
    }

    try {
      BigDecimal bd = new BigDecimal(value);
      return new SICDecimal(bd);
    } catch (NumberFormatException e) {
      return new SICString(value);
    }
  }
}
