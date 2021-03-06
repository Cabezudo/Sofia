package net.cabezudo.sofia.core.hostname;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.cabezudo.sofia.core.sites.domainname.DomainName;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class HostnameManager {

  private static final HostnameManager INSTANCE = new HostnameManager();

  public static HostnameManager getInstance() {
    return INSTANCE;
  }

  public void validate(String hostname) throws HostnameMaxSizeException, EmptyHostnameException, InvalidCharacterException, HostnameNotExistsException {
    if (hostname.isEmpty()) {
      throw new EmptyHostnameException();
    }
    if (hostname.length() > DomainName.NAME_MAX_LENGTH) {
      throw new HostnameMaxSizeException(hostname.length());
    }

    for (int i = 0; i < hostname.length(); i++) {
      Character c = hostname.charAt(i);
      if (!Character.isLetterOrDigit(c) && c != '.' && c != '-' && c != '_') {
        throw new InvalidCharacterException(c);
      }
    }

    try {
      InetAddress.getByName(hostname);
    } catch (UnknownHostException e) {
      throw new HostnameNotExistsException();
    }
  }
}
