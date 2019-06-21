package net.cabezudo.sofia.core.api.options;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.14
 */
public abstract class MultipleStringOption extends Option {

  public MultipleStringOption(String filterName, String options) {
    super(filterName, options);
    super.processMultipleStrings();
  }
}
