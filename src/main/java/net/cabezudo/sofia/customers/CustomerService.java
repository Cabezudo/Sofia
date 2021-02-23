package net.cabezudo.sofia.customers;

import java.io.IOException;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.configuration.ConfigurationException;
import net.cabezudo.sofia.core.mail.MailServer;
import net.cabezudo.sofia.core.mail.MailServerException;
import net.cabezudo.sofia.core.mail.Message;
import net.cabezudo.sofia.core.mail.Messages;
import net.cabezudo.sofia.core.passwords.Hash;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.UserManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.06
 */
public class CustomerService {

  private CustomerService() {
    // Nothing to do here. Utility classes should not have public constructors.
  }

  public static Hash sendPasswordRecoveryEMail(Site site, String address) throws MailServerException, IOException, ClusterException, ConfigurationException {
    Hash hash = new Hash();

    Message message = UserManager.getInstance().getRecoveryEMailData(site, address, hash);
    Messages messages = new Messages(message);
    MailServer.getInstance().send(messages);

    return hash;
  }

  public static void sendPasswordChangedEMail(Site site, String address) throws MailServerException, IOException, ClusterException {
    Message message = UserManager.getInstance().getPasswordChangedEMailData(site, address);
    Messages messages = new Messages(message);
    MailServer.getInstance().send(messages);
  }

  public static void sendRegistrationRetryAlert(String address) throws MailServerException, IOException, ClusterException {
    Message message = UserManager.getInstance().getRegistrationRetryAlertEMailData(address);
    Messages messages = new Messages(message);
    MailServer.getInstance().send(messages);
  }
}
