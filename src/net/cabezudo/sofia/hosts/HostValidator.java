package net.cabezudo.sofia.hosts;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.14
 */
public class HostValidator {

  public static String validate(String domainName) throws HostMaxSizeException, HostValidationException {
    try {
      HostManager.getInstance().validate(domainName);
    } catch (EmptyHostException e) {
      throw new HostValidationException("domain.empty");
    } catch (InvalidCharacterException e) {
      throw new HostValidationException("domain.invalidCharacter", e.getChar(), domainName);
    } catch (MissingDotException e) {
      throw new HostValidationException("domain.missingDot", domainName);
    } catch (HostNotExistsException e) {
      throw new HostValidationException("domain.notExists", domainName);
    }
    return "domain.ok";
  }
}
