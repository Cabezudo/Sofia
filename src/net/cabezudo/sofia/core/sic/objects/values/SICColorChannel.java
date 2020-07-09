package net.cabezudo.sofia.core.sic.objects.values;

import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICColorChannel extends SICString {

  public static final String TYPE_NAME = "colorChannel";

  public SICColorChannel(Token token) throws SICCompileTimeException {
    super(token);
    switch (token.getValue()) {
      case "red":
      case "green":
      case "blue":
        break;
      default:
        throw new SICCompileTimeException("The value must be red, green, or blue.", getToken());
    }
  }

  @Override
  public String getTypeName() {
    return TYPE_NAME;
  }

  @Override
  public boolean isColorChannel() {
    return true;
  }
}
