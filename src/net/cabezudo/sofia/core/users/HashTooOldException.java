package net.cabezudo.sofia.core.users;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.07.24
 */
public class HashTooOldException extends ChangePasswordException {

  HashTooOldException(String message) {
    super(message);
  }
}
