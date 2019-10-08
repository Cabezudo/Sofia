package net.cabezudo.sofia.emails;

import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.domainName.DomainNameMaxSizeException;
import net.cabezudo.sofia.domainName.DomainNameValidationException;
import net.cabezudo.sofia.domainName.DomainNameValidator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.14
 */
public class EMailValidator {

  public static String validate(String address) throws EMailMaxSizeException, DomainNameMaxSizeException, EMailAddressValidationException {
    Logger.finest("Validate address %s for email.", address);
    if (address.isEmpty()) {
      throw new EMailAddressValidationException("email.isEmpty", "");
    }
    if (address.length() > EMail.MAX_LENGTH) {
      throw new EMailMaxSizeException(address.length());
    }

    EMailParts eMailParts = new EMailParts(address);
    String localPart = eMailParts.getLocalPart();

    for (int j = 0; j < localPart.length(); j++) {
      char c = localPart.charAt(j);
      if (!Character.isLetterOrDigit(c) && c != '.' && c != '_') {
        throw new EMailAddressValidationException("email.invalidLocalPart", address);
      }
    }

    if (!eMailParts.hasArroba()) {
      throw new EMailAddressValidationException("email.arrobaMissing", address);
    }

    String domainName = eMailParts.getDomain();
    try {
      DomainNameValidator.validate(domainName);
    } catch (DomainNameValidationException e) {
      throw new EMailAddressValidationException(e.getMessage(), e.getParameters());
    }
    return "email.ok";
  }
}
