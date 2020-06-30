package net.cabezudo.sofia.core.sic.objects.values;

import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 * @param <T>
 */
public abstract class SICValue<T> {

  private final Token token;
  private final T value;

  public SICValue(Token token, T value) {
    this.token = token;
    this.value = value;
  }

  public T getValue() {
    return value;
  }

  public boolean isNumber() {
    return false;
  }

  public boolean isInteger() {
    return false;
  }

  public boolean isDecimal() {
    return false;
  }

  public boolean isPercentage() {
    return false;
  }

  public boolean isString() {
    return false;
  }

  public abstract String getTypeName();

  @Override
  public String toString() {
    return value.toString();
  }

  public boolean isPixels() {
    return false;
  }
}
