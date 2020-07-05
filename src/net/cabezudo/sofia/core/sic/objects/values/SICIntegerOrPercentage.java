package net.cabezudo.sofia.core.sic.objects.values;

import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.07.04
 */
public class SICIntegerOrPercentage extends SICNumberOrPercentage {

  public SICIntegerOrPercentage(Token token) throws SICCompileTimeException {
    super(token);
    if (isNumber() && isDecimal()) {
      throw new SICCompileTimeException("The parameter must be an integer.", token);
    }
  }
}
