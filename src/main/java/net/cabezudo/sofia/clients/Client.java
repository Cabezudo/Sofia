package net.cabezudo.sofia.clients;

import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.users.UserNotExistException;
import net.cabezudo.sofia.emails.EMails;
import net.cabezudo.sofia.people.Person;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.13
 */
public class Client extends Person {

  public Client(int id, String name, String lastName, EMails eMails, int owner) {
    super(id, name, lastName, eMails, owner);
  }

  public Client(Person person) throws UserNotExistException, ClusterException {
    super(person);
  }
}
