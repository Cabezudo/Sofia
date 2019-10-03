package net.cabezudo.sofia.core.passwords;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.15
 */
public class PasswordValidator {

  public static String validate(Password password) throws PasswordMaxSizeException, PasswordValidationException {

    if (password.isEmpty()) {
      throw new PasswordValidationException("password.empty");
    }
    if (password.length() < Password.MIN_LENGTH) {
      throw new PasswordValidationException("password.short");
    }
    if (password.length() > Password.MAX_LENGTH) {
      throw new PasswordMaxSizeException(password.length());
    }
    return "password.ok";
  }
}
