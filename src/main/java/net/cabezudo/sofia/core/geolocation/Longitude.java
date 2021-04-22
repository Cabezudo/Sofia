package net.cabezudo.sofia.core.geolocation;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.03.04
 */
public class Longitude {

  private final BigDecimal value;

  public Longitude(String value) throws InvalidLongitudeException {
    this.value = new BigDecimal(value).setScale(6, RoundingMode.HALF_UP);
    Longitude.validate(this.value);
  }

  public Longitude(BigDecimal value) throws InvalidLongitudeException {
    this.value = value.setScale(5);
    Longitude.validate(this.value);
  }

  public static void validate(BigDecimal longitude) throws InvalidLongitudeException {
    // (-180, 180)
    // longitude < 0 => west
    // longitude > 0 => east
    if (longitude.doubleValue() > 180 || longitude.doubleValue() < -180) {
      throw new InvalidLongitudeException();
    }
  }

  public BigDecimal getValue() {
    return value;
  }

  public double toDouble() {
    return value.doubleValue();
  }

  @Override
  public String toString() {
    return "[ longitude = " + value + "]";
  }
}
