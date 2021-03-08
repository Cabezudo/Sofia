package net.cabezudo.sofia.core.geolocation;

import java.math.BigDecimal;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.03.04
 */
public class Latitude {

  private Latitude() {
    // Utility classes should not have public constructors
  }

  public static void validate(BigDecimal latitude) throws InvalidLatitudException {
    // (-90, 90)
    // latitude < 0 => south
    // latitude > 0 => north
    if (latitude.doubleValue() > 90 || latitude.doubleValue() < -90) {
      throw new InvalidLatitudException();
    }
  }

}
