package net.cabezudo.sofia.people;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.cabezudo.sofia.core.users.UserNotExistException;
import net.cabezudo.sofia.emails.EMails;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.31
 */
public class People implements Iterable<Person> {

  List<Person> list = new ArrayList<>();
  Map<Integer, Person> map = new HashMap<>();

  @Override
  public Iterator<Person> iterator() {
    return list.iterator();
  }

  void add(Person p) throws SQLException, UserNotExistException {
    int id = p.getId();
    Person person = map.get(id);
    if (person == null) {
      list.add(p);
      map.put(id, p);
    } else {
      EMails eMails = person.getEMails();
      eMails.add(p.getEMails());
      person = new Person(id, person.getName(), person.getLastName(), eMails, p.getOwner());
      map.put(id, person);
    }
  }

}
