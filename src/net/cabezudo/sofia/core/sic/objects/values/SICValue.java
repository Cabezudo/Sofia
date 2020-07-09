package net.cabezudo.sofia.core.sic.objects.values;

import net.cabezudo.sofia.core.InvalidParameterException;
import net.cabezudo.sofia.core.sic.tokens.Position;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 * @param <T>
 */
public abstract class SICValue<T> {

  private final Token token;
  private T value;

  public SICValue(Token token) {
    if (token == null) {
      throw new InvalidParameterException("Invalid null parameter.");
    }
    this.token = token;
  }

  public Token getToken() {
    return token;
  }

  public abstract T getValue();

  public boolean isAspect() {
    return false;
  }

  public boolean isColorChannel() {
    return false;
  }

  public boolean isDecimal() {
    return false;
  }

  public boolean isImageFilePath() {
    return false;
  }

  public boolean isNumber() {
    return false;
  }

  public boolean isInteger() {
    return false;
  }

  public boolean isPercentage() {
    return false;
  }

  public boolean isPixels() {
    return false;
  }

  public boolean isString() {
    return false;
  }

  public abstract String getTypeName();

  @Override
  public String toString() {
    return token.getValue();
  }

  public Position getPosition() {
    return token.getPosition();
  }
}
