package net.cabezudo.sofia.core.sic.objects.values;

import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICPixels extends SICInteger {

  public SICPixels(Token token, Integer value) {
    super(token, value);
  }

  public SICPixels(Token token, SICInteger number) {
    super(token, number.getValue());
  }

  @Override
  public String toString() {
    return getValue() + "px";
  }

  @Override
  public String getTypeName() {
    return "pixels";
  }

  @Override
  public boolean isPixels() {
    return true;
  }
}
