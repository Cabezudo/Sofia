package net.cabezudo.sofia.emails;

import java.sql.SQLException;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.people.PeopleManager;
import net.cabezudo.sofia.people.Person;
import net.cabezudo.sofia.people.PersonNotExistException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.17
 */
public class EMail implements Comparable<EMail> {

  public final static int MAX_LENGTH = 150;

  private final int id;
  private final int personId;
  private Person person;
  private final String address;
  private final EMailParts eMailParts;

  public EMail(int id, int personId, String address) {
    this.id = id;
    this.personId = personId;
    this.address = address;

    eMailParts = new EMailParts(address);
  }

  @Override
  public int compareTo(EMail eMail) {
    return this.getAddress().compareTo(eMail.getAddress());
  }

  public int getId() {
    return id;
  }

  public int getPersonId() {
    return personId;
  }

  public Person getPerson() throws PersonNotExistException, SQLException {
    if (person == null) {
      person = PeopleManager.getInstance().get(personId);
      if (person == null) {
        throw new PersonNotExistException("Can't find the site with the id " + personId, id);
      }
    }
    return person;
  }

  public String getAddress() {
    return address;
  }

  public boolean isEmpty() {
    return address.isEmpty();
  }

  public int length() {
    return address.length();
  }

  public String getLocalPart() {
    return eMailParts.getLocalPart();
  }

  boolean hasArroba() {
    return eMailParts.hasArroba();
  }

  public String getDomain() {
    return eMailParts.getDomain();
  }

  @Override
  public String toString() {
    return "[ id = " + id + ", address = " + address + ", localPart = " + getLocalPart() + ", hasArroba = " + hasArroba() + ", domain = " + getDomain() + "]";
  }

  public JSONValue toJSON() {
    JSONObject jsonEMailObject = new JSONObject();
    jsonEMailObject.add(new JSONPair("id", getId()));
    jsonEMailObject.add(new JSONPair("address", getAddress()));
    return jsonEMailObject;
  }
}
