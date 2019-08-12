package net.cabezudo.sofia.people;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.EntityList;
import net.cabezudo.sofia.core.users.UserNotExistException;
import net.cabezudo.sofia.emails.EMail;
import net.cabezudo.sofia.emails.EMails;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.16
 */
public class PeopleList extends EntityList<Person> {

  List<Person> list = new ArrayList<>();
  Map<Integer, Person> map = new HashMap<>();

  public PeopleList(int offset, int pageSize) {
    super(offset, pageSize);
  }

  @Override
  public Iterator<Person> iterator() {
    return list.iterator();
  }

  public void add(Person p) throws SQLException, UserNotExistException {
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

  @Override
  public String toJSON() {
    return toJSONTree().toString();
  }

  @Override
  public JSONValue toJSONTree() {
    JSONObject listObject = new JSONObject();
    JSONArray jsonRecords = new JSONArray();
    JSONPair jsonRecordsPair = new JSONPair("list", jsonRecords);
    listObject.add(jsonRecordsPair);
    for (Person person : list) {
      JSONObject jsonPerson = new JSONObject();
      jsonPerson.add(new JSONPair("id", person.getId()));
      jsonPerson.add(new JSONPair("name", person.getName()));
      jsonPerson.add(new JSONPair("lastName", person.getLastName()));
      JSONArray jsonMails = new JSONArray();
      jsonPerson.add(new JSONPair("userNames", jsonMails));
      EMails eMails = person.getEMails();
      for (EMail eMail : eMails) {
        JSONObject jsonEMail = new JSONObject();
        jsonEMail.add(new JSONPair("id", eMail.getId()));
        jsonEMail.add(new JSONPair("address", eMail.getAddress()));
        jsonMails.add(jsonEMail);
      }
      jsonRecords.add(jsonPerson);
    }

    return listObject;
  }
}
