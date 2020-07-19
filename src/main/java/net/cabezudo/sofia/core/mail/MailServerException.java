package net.cabezudo.sofia.core.mail;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.11
 */
public class MailServerException extends Exception {

  public MailServerException(Throwable cause) {
    super(cause);
  }

  public MailServerException(String message) {
    super(message);
  }
}
