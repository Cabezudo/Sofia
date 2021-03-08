package net.cabezudo.sofia.core.list;

import net.cabezudo.sofia.core.api.options.MultipleStringOption;
import net.cabezudo.sofia.core.api.options.OptionValue;
import net.cabezudo.sofia.core.api.options.StringOptionValue;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.14
 */
public class Filters extends MultipleStringOption {

  public Filters(String filters) {
    super(FILTERS, filters);
  }

  @Override
  protected OptionValue createOptionValue(String v) {
    return new StringOptionValue(v);
  }
}
