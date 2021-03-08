package net.cabezudo.sofia.core.geolocation;

import java.math.BigDecimal;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.03.04
 */
public class Longitude {

  private Longitude() {
    // Utility classes should not have public constructors
  }

  public static void validate(BigDecimal longitude) throws InvalidLongitudeException {
    // (-180, 180)
    // longitude < 0 => west
    // longitude > 0 => east
    if (longitude.doubleValue() > 180 || longitude.doubleValue() < -180) {
      throw new InvalidLongitudeException();
    }
  }
}
