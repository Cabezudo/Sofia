package net.cabezudo.sofia.core.sites.domainname;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.14
 */
public class DomainNameValidator {

  public static String validate(String domainName) throws DomainNameMaxSizeException, DomainNameValidationException {
    try {
      DomainNameManager.getInstance().validate(domainName);
    } catch (EmptyDomainNameException e) {
      throw new DomainNameValidationException("domain.empty", domainName);
    } catch (InvalidCharacterException e) {
      throw new DomainNameValidationException("domain.invalidCharacter", e.getChar(), domainName);
    } catch (MissingDotException e) {
      throw new DomainNameValidationException("domain.missingDot", domainName);
    } catch (DomainNameNotExistsException e) {
      throw new DomainNameValidationException("domain.notExists", domainName);
    }
    return "domain.ok";
  }
}
