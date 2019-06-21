package net.cabezudo.sofia.phonenumbers;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.04.18
 */
public class PhoneNumber {

  public static final int MAX_LENGTH = 14;

  private final long number;

  public PhoneNumber(long number) {
    this.number = number;
  }

  public long getNumber() {
    return number;
  }
}
