package net.cabezudo.sofia.core.users.autentication;

import java.sql.SQLException;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.core.ws.responses.Messages;
import net.cabezudo.sofia.hosts.HostMaxSizeException;
import net.cabezudo.sofia.emails.EMailMaxSizeException;
import net.cabezudo.sofia.emails.EMailValidator;
import net.cabezudo.sofia.core.passwords.Password;
import net.cabezudo.sofia.core.passwords.PasswordMaxSizeException;
import net.cabezudo.sofia.core.passwords.PasswordValidator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.16
 */
public class Authenticator {

  private final Messages messages = new Messages();

  public User authorize(Site site, String address, Password password) throws EMailMaxSizeException, HostMaxSizeException, PasswordMaxSizeException, SQLException {
    messages.add(EMailValidator.validate(address));
    messages.add(PasswordValidator.validate(password));

    User user = UserManager.getInstance().login(site, address, password);

    return user;
  }

  public Messages getMessages() {
    return messages;
  }
}
