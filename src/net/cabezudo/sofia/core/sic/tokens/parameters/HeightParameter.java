package net.cabezudo.sofia.core.sic.tokens.parameters;

import net.cabezudo.sofia.core.sic.elements.SICParameter;
import net.cabezudo.sofia.core.sic.objects.SICObject;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class HeightParameter extends SICParameter {

  public HeightParameter(Token name, Token value) {
    super(name, value);
  }

  @Override
  public boolean isHeightParameter() {
    return true;
  }

  @Override
  public SICObject compile() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
