package net.cabezudo.sofia.emails;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.08
 */
public class EMailParts {

  private final String localPart;
  private final boolean hasArroba;
  private final String domain;

  public EMailParts(String address) {
    int i = address.indexOf("@");
    if (i == -1) {
      this.localPart = address;
      this.hasArroba = false;
    } else {
      this.localPart = address.substring(0, i);
      this.hasArroba = true;
    }

    this.domain = address.substring(i + 1);

  }

  public String getLocalPart() {
    return localPart;
  }

  public boolean hasArroba() {
    return hasArroba;
  }

  public String getDomain() {
    return domain;
  }
}
