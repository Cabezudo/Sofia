package net.cabezudo.sofia.core.api.options;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.23
 */
public abstract class OptionValue {

  private final Object value;
  private final Sign sign;

  public enum Sign {
    NONE, POSITIVE, NEGATIVE
  }

  OptionValue(String v) {
    char c = v.charAt(0);

    switch (c) {
      case '+':
        value = v.substring(1);
        sign = Sign.POSITIVE;
        break;
      case '-':
        value = v.substring(1);
        sign = Sign.NEGATIVE;
        break;
      default:
        sign = Sign.POSITIVE;
        value = v;
        break;
    }
  }

  OptionValue(Integer value) {
    this.value = value;
    sign = Sign.NONE;
  }

  public boolean isPositive() {
    return sign == Sign.POSITIVE;
  }

  public boolean isNegative() {
    return sign == Sign.NEGATIVE;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public String toString() {
    return (sign == Sign.POSITIVE ? "+" : "-") + value.toString();
  }
}
