package net.cabezudo.sofia.clients;

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
import net.cabezudo.sofia.emails.EMail;
import net.cabezudo.sofia.emails.EMails;
import net.cabezudo.sofia.core.users.UserNotExistException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.13
 */
public class ClientList extends EntityList<Client> {

  List<Client> list = new ArrayList<>();
  Map<Integer, Client> map = new HashMap<>();

  @Override
  public Iterator<Client> iterator() {
    return list.iterator();
  }

  public void add(Client p) throws SQLException, UserNotExistException {
    int id = p.getId();
    Client client = map.get(id);
    if (client == null) {
      list.add(p);
      map.put(id, p);
    } else {
      EMails eMails = client.getEMails();
      eMails.add(p.getEMails());
      client = new Client(id, client.getName(), client.getLastName(), eMails, client.getOwner().getId());
      map.put(id, client);
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
    JSONPair jsonRecordsPair = new JSONPair("records", jsonRecords);
    listObject.add(jsonRecordsPair);
    list.forEach((client) -> {
      JSONObject jsonPerson = new JSONObject();
      jsonPerson.add(new JSONPair("id", client.getId()));
      jsonPerson.add(new JSONPair("name", client.getName()));
      jsonPerson.add(new JSONPair("lastName", client.getLastName()));
      JSONArray jsonMails = new JSONArray();
      jsonPerson.add(new JSONPair("userNames", jsonMails));
      EMails eMails = client.getEMails();
      for (EMail eMail : eMails) {
        JSONObject jsonEMail = new JSONObject();
        jsonEMail.add(new JSONPair("id", eMail.getId()));
        jsonEMail.add(new JSONPair("address", eMail.getAddress()));
        jsonMails.add(jsonEMail);
      }
      jsonRecords.add(jsonPerson);
    });

    return listObject;
  }
}
