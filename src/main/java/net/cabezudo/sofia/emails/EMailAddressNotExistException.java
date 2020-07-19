package net.cabezudo.sofia.emails;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.08
 */
public class EMailAddressNotExistException extends Exception {

  private final String address;

  public EMailAddressNotExistException(String message, String address) {
    super(message);
    this.address = address;
  }

  public String getAddress() {
    return address;
  }
}
