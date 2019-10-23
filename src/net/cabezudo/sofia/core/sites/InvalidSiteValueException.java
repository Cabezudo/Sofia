package net.cabezudo.sofia.core.sites;

import java.util.Arrays;
import net.cabezudo.sofia.core.ParametrizedException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.21
 */
public class InvalidSiteValueException extends ParametrizedException {

  private final Object[] values;

  public InvalidSiteValueException(String message, Object... values) {
    super(message + ": " + Arrays.toString(values));
    this.values = values;
  }

  @Override
  public Object[] getParameters() {
    return values;
  }
}
