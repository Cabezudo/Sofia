package net.cabezudo.sofia.core.sic.objects;

import net.cabezudo.sofia.core.sic.objects.values.SICPercentage;
import net.cabezudo.sofia.core.sic.objects.values.SICPixels;
import net.cabezudo.sofia.core.sic.objects.values.SICValue;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public abstract class SICObjectFunction extends SICObject {

  protected int getPixels(SICValue<?> value, int height) {
    if (value.isPixels()) {
      SICPixels pixels = (SICPixels) value;
      return pixels.getValue();
    } else {
      SICPercentage sicPercentage = (SICPercentage) value;
      double percentage = Double.parseDouble(sicPercentage.getValue().toString());
      return (int) (percentage / 100 * height);
    }
  }
}
