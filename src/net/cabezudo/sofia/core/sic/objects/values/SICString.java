package net.cabezudo.sofia.core.sic.objects.values;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICString extends SICValue<String> {

  public SICString(String value) {
    super(value);
  }

  @Override
  public String getTypeName() {
    return "string";
  }

  @Override
  public boolean isString() {
    return true;
  }
}
