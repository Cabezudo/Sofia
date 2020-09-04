package net.cabezudo.sofia.restaurants;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.02
 */
public class DeliveryRange {

  private final int min;
  private final int max;

  public DeliveryRange(int min, int max) {
    this.min = min;
    this.max = max;
  }

  public DeliveryRange(int value) {
    this.min = value;
    this.max = value;
  }

  int getMin() {
    return min;
  }

  int getMax() {
    return max;
  }

  Object toJSONTree() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
