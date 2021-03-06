package net.cabezudo.sofia.core.geolocation;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.03.04
 */
public class Latitude {

  private final BigDecimal value;

  public Latitude(String value) throws InvalidLatitudeException {
    this.value = new BigDecimal(value).setScale(6, RoundingMode.HALF_UP);
    Latitude.validate(this.value);
  }

  public Latitude(BigDecimal value) throws InvalidLatitudeException {
    this.value = value.setScale(5);
    Latitude.validate(this.value);
  }

  public static void validate(BigDecimal latitude) throws InvalidLatitudeException {
    // (-90, 90)
    // latitude < 0 => south
    // latitude > 0 => north
    if (latitude.doubleValue() > 90 || latitude.doubleValue() < -90) {
      throw new InvalidLatitudeException();
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
    return "[ latitude = " + value + "]";
  }
}
