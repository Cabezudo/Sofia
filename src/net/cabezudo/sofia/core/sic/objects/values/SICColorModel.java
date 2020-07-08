package net.cabezudo.sofia.core.sic.objects.values;

import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICColorModel extends SICString {

  public SICColorModel(Token token) throws SICCompileTimeException {
    super(token);
    switch (token.getValue()) {
      case "tv":
      case "hsb":
        break;
      default:
        throw new SICCompileTimeException("Invalid model for brightness.", token);
    }
  }

  @Override
  public String getTypeName() {
    return "colorModel";
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
