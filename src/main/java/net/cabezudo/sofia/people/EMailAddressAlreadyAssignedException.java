package net.cabezudo.sofia.people;

import net.cabezudo.sofia.emails.EMail;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.11
 */
public class EMailAddressAlreadyAssignedException extends Exception {

  private final EMail eMailAddress;
  private final Person person;

  public EMailAddressAlreadyAssignedException(EMail eMailAddress, Person person) {
    this.eMailAddress = eMailAddress;
    this.person = person;
  }

  public EMail geteMailAddress() {
    return eMailAddress;
  }

  public Person getPerson() {
    return person;
  }
}
