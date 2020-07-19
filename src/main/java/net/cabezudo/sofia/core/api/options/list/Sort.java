package net.cabezudo.sofia.core.api.options.list;

import net.cabezudo.sofia.core.api.options.Option;
import net.cabezudo.sofia.core.api.options.OptionValue;
import net.cabezudo.sofia.core.api.options.StringOptionValue;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.14
 */
public class Sort extends Option {

  public Sort(String criteria) {
    super(SORT, criteria);
    super.processMultipleStrings();
  }

  @Override
  protected OptionValue createOptionValue(String v) {
    return new StringOptionValue(v);
  }
}
