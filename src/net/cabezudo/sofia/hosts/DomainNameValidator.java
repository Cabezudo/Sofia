package net.cabezudo.sofia.hosts;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.14
 */
public class DomainNameValidator {

  public static String validate(String domainName) throws HostMaxSizeException, DomainNameValidationException {
    try {
      HostManager.getInstance().validate(domainName);
    } catch (EmptyHostException e) {
      throw new DomainNameValidationException("domain.empty", domainName);
    } catch (InvalidCharacterException e) {
      throw new DomainNameValidationException("domain.invalidCharacter", domainName);
    } catch (MissingDotException e) {
      throw new DomainNameValidationException("domain.missingDot", domainName);
    } catch (HostNotExistsException e) {
      throw new DomainNameValidationException("domain.notExists", domainName);
    }
    return "domain.ok";
  }
}
