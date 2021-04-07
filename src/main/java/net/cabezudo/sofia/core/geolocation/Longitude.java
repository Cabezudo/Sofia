package net.cabezudo.sofia.core.geolocation;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.03.04
 */
public class Longitude {

  private final BigDecimal value;

  public Longitude(String value) {
    this.value = new BigDecimal(value).setScale(6, RoundingMode.HALF_UP);
  }

  public Longitude(BigDecimal value) {
    this.value = value.setScale(5);
  }

  public BigDecimal getValue() {
    return value;
  }

  public static void validate(BigDecimal longitude) throws InvalidLongitudeException {
    // (-180, 180)
    // longitude < 0 => west
    // longitude > 0 => east
    if (longitude.doubleValue() > 180 || longitude.doubleValue() < -180) {
      throw new InvalidLongitudeException();
    }
  }

  @Override
  public String toString() {
    return "[ longitude = " + value + "]";
  }
}
