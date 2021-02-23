package net.cabezudo.sofia.people;

import java.util.Locale;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.JSONable;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.core.users.UserNotExistException;
import net.cabezudo.sofia.emails.EMails;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.16
 */
public class Person implements JSONable {

  private final int id;
  private final String name;
  private final String lastName;
  private final EMails eMails;
  private final int ownerId;
  private User owner;

  public Person(Person person) throws UserNotExistException, ClusterException {
    this.id = person.getId();
    this.name = person.getName();
    this.lastName = person.getLastName();
    this.eMails = new EMails(person.getEMails());
    this.owner = person.getOwner();
    this.ownerId = this.owner.getId();
  }

  public Person(int id, String name, String lastName, EMails eMails, User owner) {
    this.id = id;
    this.name = name;
    this.lastName = lastName;
    this.eMails = new EMails(eMails);
    this.ownerId = owner.getId();
    this.owner = owner;
  }

  public Person(int id, String name, String lastName, EMails eMails, int ownerId) {
    this.id = id;
    this.name = name;
    this.lastName = lastName;
    this.eMails = new EMails(eMails);
    this.ownerId = ownerId;
  }

  public Person(int id, String name, String lastName, int ownerId) {
    this(id, name, lastName, new EMails(), ownerId);
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getLastName() {
    return lastName;
  }

  public EMails getEMails() {
    return eMails;
  }

  public Locale getLocale() {
    return new Locale("es");
  }

  @Override
  public String toString() {
    return "[" + id + ", " + name + ", " + lastName + "]";
  }

  @Override
  public String toJSON() {
    return this.toJSONTree().toJSON();
  }

  public int getOwnerId() {
    return ownerId;
  }

  public User getOwner() throws UserNotExistException, ClusterException {
    if (owner == null) {
      owner = UserManager.getInstance().get(ownerId);
      if (owner == null) {
        throw new UserNotExistException("Can't find the user with the id " + ownerId, id);
      }
    }
    return owner;
  }

  @Override
  public JSONValue toJSONTree() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("id", id));
    jsonObject.add(new JSONPair("name", name));
    jsonObject.add(new JSONPair("lastName", lastName));
    jsonObject.add(new JSONPair("eMails", eMails.toJSONTree()));
    return jsonObject;
  }
}
