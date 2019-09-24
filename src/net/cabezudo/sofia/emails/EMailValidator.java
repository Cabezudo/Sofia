package net.cabezudo.sofia.emails;

import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.ws.responses.ErrorMessage;
import net.cabezudo.sofia.core.ws.responses.Messages;
import net.cabezudo.sofia.core.ws.responses.ValidMessage;
import net.cabezudo.sofia.hosts.HostMaxSizeException;
import net.cabezudo.sofia.hosts.DomainNameValidator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.14
 */
public class EMailValidator {

  public static Messages validate(String address) throws EMailMaxSizeException, HostMaxSizeException {
    Logger.finest("Validate address %s for email.", address);
    Messages messages = new Messages();
    if (address.isEmpty()) {
      messages.add(new ErrorMessage("email.isEmpty"));
      return messages;
    }
    if (address.length() > EMail.MAX_LENGTH) {
      throw new EMailMaxSizeException(address.length());
    }

    EMailParts eMailParts = new EMailParts(address);
    String localPart = eMailParts.getLocalPart();

    for (int j = 0; j < localPart.length(); j++) {
      char c = localPart.charAt(j);
      if (!Character.isLetterOrDigit(c) && c != '.' && c != '_') {
        messages.add(new ErrorMessage("email.invalidLocalPart", address, c));
        return messages;
      }
    }

    if (!eMailParts.hasArroba()) {
      messages.add(new ErrorMessage("email.arrobaMissing"));
      return messages;
    }

    String domainName = eMailParts.getDomain();
    Messages domainMessages = DomainNameValidator.validate(domainName);
    if (domainMessages.hasErrors()) {
      return domainMessages;
    }
    messages.add(new ValidMessage("email.ok"));
    return messages;
  }
}
