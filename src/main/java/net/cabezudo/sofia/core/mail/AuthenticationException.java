package net.cabezudo.sofia.core.mail;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.11
 */
public class AuthenticationException extends MailServerException {

  public AuthenticationException(Throwable cause) {
    super(cause);
  }
}
