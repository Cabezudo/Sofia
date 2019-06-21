package net.cabezudo.sofia.emails;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.08
 */
public class EMailNotExistException extends Exception {

  private final int id;

  public EMailNotExistException(String message, int id) {
    super(message);
    this.id = id;
  }

  public int getId() {
    return id;
  }
}
