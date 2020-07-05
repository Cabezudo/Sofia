package net.cabezudo.sofia.core.sic.objects.values;

import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICString extends SICValue<String> {

  public SICString(Token token) {
    super(token);
  }

  @Override
  public String getTypeName() {
    return "string";
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
