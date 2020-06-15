package net.cabezudo.sofia.core.sic.objects.values;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 * @param <T>
 */
public abstract class SICNumber<T> extends SICValue<T> {

  public SICNumber(T value) {
    super(value);
  }

  @Override
  public boolean isNumber() {
    return true;
  }

  public abstract boolean isZero();
}
