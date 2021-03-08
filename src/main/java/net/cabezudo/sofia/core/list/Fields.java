package net.cabezudo.sofia.core.list;

import net.cabezudo.sofia.core.api.options.Option;
import net.cabezudo.sofia.core.api.options.OptionValue;
import net.cabezudo.sofia.core.api.options.StringOptionValue;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.14
 */
public class Fields extends Option {

  public Fields(String fields) {
    super(FIELDS, fields);
    super.processMultipleStrings();
  }

  @Override
  protected OptionValue createOptionValue(String v) {
    return new StringOptionValue(v);
  }
}
