package net.cabezudo.sofia.people;

import java.util.Iterator;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.list.EntryList;
import net.cabezudo.sofia.core.users.UserNotExistException;
import net.cabezudo.sofia.emails.EMail;
import net.cabezudo.sofia.emails.EMails;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.16
 */
public class PeopleList extends EntryList<Person> {

  People people = new People();

  public PeopleList(int offset, int pageSize) {
    super(offset, pageSize);
  }

  @Override
  public Iterator<Person> iterator() {
    return people.iterator();
  }

  public void add(Person person) throws UserNotExistException, ClusterException {
    people.add(person);
  }

  @Override
  public JSONValue toJSONTree() {
    JSONObject listObject = new JSONObject();
    JSONArray jsonRecords = new JSONArray();
    JSONPair jsonRecordsPair = new JSONPair("list", jsonRecords);
    listObject.add(jsonRecordsPair);
    for (Person person : people) {
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

  @Override
  public void toFormatedString(StringBuilder sb, int indent, boolean includeFirst) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
