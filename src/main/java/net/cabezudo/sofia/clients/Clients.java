package net.cabezudo.sofia.clients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.users.UserNotExistException;
import net.cabezudo.sofia.emails.EMails;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.31
 */
public class Clients implements Iterable<Client> {

  List<Client> list = new ArrayList<>();
  Map<Integer, Client> map = new HashMap<>();

  @Override
  public Iterator<Client> iterator() {
    return list.iterator();
  }

  void add(Client p) throws UserNotExistException, ClusterException {
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

}
