package net.cabezudo.sofia.core.list;

import net.cabezudo.sofia.core.api.options.IntegerOptionValue;
import net.cabezudo.sofia.core.api.options.Option;
import net.cabezudo.sofia.core.api.options.OptionValue;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.14
 */
public class Limit extends Option {

  public Limit(Integer value) {
    super(LIMIT, value);
  }

  @Override
  protected OptionValue createOptionValue(String value) {
    int integer = Integer.parseInt(value);
    return new IntegerOptionValue(integer);
  }
}
