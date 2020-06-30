package net.cabezudo.sofia.core.sic.objects;

import net.cabezudo.sofia.core.sic.objects.values.SICNumber;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.26
 */
public class SICInvalidValue extends SICNumber<String> {

  public SICInvalidValue(Token token) {
    super(token, token.getValue());
  }

  @Override
  public boolean isZero() {
    return false;
  }

  @Override
  public String getTypeName() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
