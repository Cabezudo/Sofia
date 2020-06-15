package net.cabezudo.sofia.core.sic.objects.values;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICInteger extends SICNumber<Integer> {

  public SICInteger(Integer value) {
    super(value);
  }

  @Override
  public String getTypeName() {
    return "number";
  }

  @Override
  public boolean isInteger() {
    return true;
  }

  @Override
  public boolean isZero() {
    return getValue() == 0;
  }
}
