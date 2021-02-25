package net.cabezudo.sofia.core.users;

import java.util.Iterator;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.EntityList;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.emails.EMail;
import net.cabezudo.sofia.emails.EMailNotExistException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.02.24
 */
public class UserList extends EntityList<User> {

  Users users = new Users();

  public UserList(int offset, int pageSize) {
    super(offset, pageSize);
  }

  @Override
  public Iterator<User> iterator() {
    return users.iterator();
  }

  public void add(User user) throws UserNotExistException, ClusterException {
    users.add(user);
  }

  @Override
  public JSONValue toJSONTree() {
    JSONObject listObject = new JSONObject();
    JSONArray jsonRecords = new JSONArray();
    JSONPair jsonRecordsPair = new JSONPair("list", jsonRecords);
    listObject.add(jsonRecordsPair);
    for (User user : users) {
      JSONObject jsonUser = new JSONObject();
      jsonUser.add(new JSONPair("id", user.getId()));
      EMail eMail;
      String address = null;
      try {
        eMail = user.getMail();
        address = eMail.getAddress();
      } catch (EMailNotExistException e) {
        throw new SofiaRuntimeException(e);
      }
      jsonUser.add(new JSONPair("eMail", address));
      jsonUser.add(new JSONPair("creationDate", Utils.getDateToString(user.getCreationDate())));
      jsonUser.add(new JSONPair("isActive", user.isActivated()));
      jsonUser.add(new JSONPair("passwordRecoveryHash", user.getPasswordRecoveryHash()));
      jsonUser.add(new JSONPair("passwordRecoveryDate", Utils.getDateToString(user.getPasswordRecoveryDate())));
      jsonRecords.add(jsonUser);
    }

    return listObject;
  }
}
