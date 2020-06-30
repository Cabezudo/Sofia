package net.cabezudo.sofia.core.sic.elements;

import net.cabezudo.sofia.core.sic.SICCompilerMessages;
import net.cabezudo.sofia.core.sic.objects.SICObject;
import net.cabezudo.sofia.core.sic.tokens.Position;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class SICUnfinishedFunction extends SICParameterOrFunction {

  public SICUnfinishedFunction(String name, Position position) {
    super(name, position);
  }

  @Override
  public String toString(int i) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public SICObject compile(SICCompilerMessages messages) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
