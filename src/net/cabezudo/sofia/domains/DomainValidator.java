package net.cabezudo.sofia.domains;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.cabezudo.sofia.core.ws.responses.ErrorMessage;
import net.cabezudo.sofia.core.ws.responses.Message;
import net.cabezudo.sofia.core.ws.responses.Messages;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.14
 */
public class DomainValidator {

  public static Messages validate(String domainName) throws DomainMaxSizeException {
    Messages messages = new Messages();

    if (domainName.isEmpty()) {
      messages.add(new ErrorMessage("domain.empty"));
      return messages;
    }
    if (domainName.length() > DomainName.NAME_MAX_LENGTH) {
      throw new DomainMaxSizeException(domainName.length());
    }

    int dotCounter = 0;
    for (int i = 0; i < domainName.length(); i++) {
      Character c = domainName.charAt(i);
      if (!Character.isLetterOrDigit(c) && c != '.' && c != '-' && c != '_') {
        messages.add(new ErrorMessage("domain.invalidCharacter", c, domainName));
        return messages;
      }
      if (c == '.') {
        dotCounter++;
      }
    }
    if (dotCounter == 0) {
      messages.add(new ErrorMessage("domain.missingDot", domainName));
      return messages;
    }

    try {
      InetAddress.getByName(domainName);
    } catch (UnknownHostException e) {
      messages.add(new ErrorMessage("domain.notExists", domainName));
      return messages;
    }
    messages.add(new Message("domain.ok"));
    return messages;
  }
}
