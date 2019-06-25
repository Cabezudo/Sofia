package net.cabezudo.sofia.domains;

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

    try {
      DomainNameManager.getInstance().validate(domainName);
    } catch (EmptyDomainNameException e) {
      messages.add(new ErrorMessage("domain.empty"));
      return messages;
    } catch (InvalidCharacterException e) {
      messages.add(new ErrorMessage("domain.invalidCharacter", e.getChar(), domainName));
      return messages;
    } catch (MissingDotException e) {
      messages.add(new ErrorMessage("domain.missingDot", domainName));
      return messages;
    } catch (DomainNameNotExistsException e) {
      messages.add(new ErrorMessage("domain.notExists", domainName));
      return messages;
    }
    messages.add(new Message("domain.ok"));
    return messages;
  }
}
