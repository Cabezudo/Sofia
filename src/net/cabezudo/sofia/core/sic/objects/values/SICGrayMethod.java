package net.cabezudo.sofia.core.sic.objects.values;

import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICGrayMethod extends SICString {

  public SICGrayMethod(Token token) throws SICCompileTimeException {
    super(token);
    switch (token.getValue()) {
      case "averaging":
      case "luma":
      case "desaturation":
      case "decomposition":
      case "colorChanel":
      case "grayShades":
        break;
      default:
        throw new SICCompileTimeException("Invalid model for brightness.", token);
    }
  }

  @Override
  public String getTypeName() {
    return "grayMethod";
  }

  @Override
  public boolean isString() {
    return true;
  }

  @Override
  public String getValue() {
    return getToken().getValue();
  }
}
