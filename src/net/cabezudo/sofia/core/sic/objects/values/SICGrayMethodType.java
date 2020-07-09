package net.cabezudo.sofia.core.sic.objects.values;

import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICGrayMethodType extends SICString {

  public SICGrayMethodType(Token grayMethodToken, Token grayMethodTypeToken) throws SICCompileTimeException {
    super(grayMethodTypeToken);
    switch (grayMethodToken.getValue()) {
      case "luma":
        switch (grayMethodTypeToken.getValue()) {
          case "basic":
          case "bt709":
          case "bt601":
            break;
          default:
            throw new SICCompileTimeException("Invalid method type for Luma model.", grayMethodTypeToken);
        }
        break;
      case "decomposition":
        switch (grayMethodTypeToken.getValue()) {
          case "maximum":
          case "minimum":
            break;
          default:
            throw new SICCompileTimeException("Invalid method type for decomposition model.", grayMethodTypeToken);
        }
        break;
      case "colorChannel":
        switch (grayMethodTypeToken.getValue()) {
          case "red":
          case "green":
          case "blue":
            break;
          default:
            throw new SICCompileTimeException("Invalid method type for single color channel model.", grayMethodTypeToken);
        }
        break;
      case "grayShades":
      case "averaging":
      case "desaturation":
        break;
      default:
        throw new SICCompileTimeException("Invalid model for brightness.", grayMethodToken);
    }
  }

  @Override
  public String getTypeName() {
    return "grayMethodType";
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
