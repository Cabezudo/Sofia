package net.cabezudo.sofia.core.sic.elements;

import net.cabezudo.sofia.core.sic.exceptions.SICCompilerException;
import net.cabezudo.sofia.core.sic.objects.SICObject;
import net.cabezudo.sofia.core.sic.tokens.Position;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public abstract class SICElement {

  private final Position position;

  public SICElement(Position position) {
    this.position = position;
  }

  public Position getPosition() {
    return position;
  }

  public abstract String toString(int i);

  public abstract SICObject compile() throws SICCompilerException;
}
