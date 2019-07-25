package net.cabezudo.sofia.core.users;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.07.24
 */
public class UserNotFoundByHashException extends ChangePasswordException {

  public UserNotFoundByHashException(String message) {
    super(message);
  }
}
