package net.cabezudo.sofia.core.sic.objects.values;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICPixels extends SICInteger {

  public SICPixels(Integer value) {
    super(value);
  }

  public SICPixels(SICInteger number) {
    super(number.getValue());
  }

  @Override
  public String toString() {
    return getValue() + "px";
  }

  @Override
  public String getTypeName() {
    return "pixels";
  }

  @Override
  public boolean isPixels() {
    return true;
  }
}
