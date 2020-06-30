package net.cabezudo.sofia.core.sic.elements;

import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.sic.SICCompilerMessages;
import net.cabezudo.sofia.core.sic.objects.SICInvalidObject;
import net.cabezudo.sofia.core.sic.objects.SICObject;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.25
 */
public class SICInvalidElement extends SICElement {

  private final String value;

  public SICInvalidElement(Token token) {
    super(token.getPosition());
    this.value = token.getValue();
  }

  @Override
  public String toString(int deep) {
    return Utils.repeat(' ', deep * 2) + value;
  }

  @Override
  public SICObject compile(SICCompilerMessages messages) {
    return new SICInvalidObject();
  }
}
