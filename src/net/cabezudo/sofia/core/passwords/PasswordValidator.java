package net.cabezudo.sofia.core.passwords;

import net.cabezudo.sofia.core.ws.responses.ErrorMessage;
import net.cabezudo.sofia.core.ws.responses.Messages;
import net.cabezudo.sofia.core.ws.responses.ValidMessage;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.15
 */
public class PasswordValidator {

  public static Messages validate(Password password) throws PasswordMaxSizeException {
    Messages messages = new Messages();

    if (password.isEmpty()) {
      messages.add(new ErrorMessage("password.empty"));
      return messages;
    }
    if (password.length() < Password.MIN_LENGTH) {
      messages.add(new ErrorMessage("password.short"));
      return messages;
    }
    if (password.length() > Password.MAX_LENGTH) {
      throw new PasswordMaxSizeException(password.length());
    }
    messages.add(new ValidMessage("password.ok"));
    return messages;
  }
}
