package net.cabezudo.sofia.core.users;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.list.EntryList;
import net.cabezudo.sofia.emails.EMail;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.02.24
 */
public class UserList extends EntryList<UserForList> {

  private final List<UserForList> list = new ArrayList<>();

  public UserList(int offset, int pageSize) {
    super(offset, pageSize);
  }

  @Override
  public Iterator<UserForList> iterator() {
    return list.iterator();
  }

  public void add(UserForList user) throws ClusterException {
    list.add(user);
  }

  @Override
  public JSONValue toJSONTree() {
    JSONObject listObject = new JSONObject();
    JSONArray jsonRecords = new JSONArray();
    JSONPair jsonRecordsPair = new JSONPair("list", jsonRecords);
    listObject.add(jsonRecordsPair);
    int row = super.getOffset();
    for (UserForList user : list) {
      JSONObject jsonUser = new JSONObject();
      jsonUser.add(new JSONPair("id", user.getId()));
      EMail eMail;
      String address = null;
      eMail = user.getEMail();
      address = eMail.getAddress();
      jsonUser.add(new JSONPair("row", row));
      jsonUser.add(new JSONPair("siteName", user.getSiteName()));
      jsonUser.add(new JSONPair("eMail", address));
      jsonUser.add(new JSONPair("creationDate", Utils.getDateToString(user.getCreationDate())));
      jsonUser.add(new JSONPair("isActive", user.isActivated()));
      jsonRecords.add(jsonUser);
      row++;
    }

    return listObject;
  }

  @Override
  public void toFormatedString(StringBuilder sb, int indent, boolean includeFirst) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
