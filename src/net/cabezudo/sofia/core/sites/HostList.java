package net.cabezudo.sofia.core.sites;

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
import net.cabezudo.sofia.hosts.Host;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class HostList extends EntityList<Host> {

  List<Host> list = new ArrayList<>();
  Map<Integer, Host> map = new HashMap<>();

  public HostList(int offset, int pageSize) {
    super(offset, pageSize);
  }

  @Override
  public Iterator<Host> iterator() {
    return list.iterator();
  }

  public void add(Host h) throws SQLException {
    int id = h.getId();
    Host host = map.get(id);
    if (host == null) {
      list.add(h);
      map.put(id, h);
    } else {
      host = new Host(id, host.getName());
      map.put(id, host);
    }
  }

  @Override
  public String toJSON() {
    return toJSONTree().toString();
  }

  @Override
  public JSONValue toJSONTree() {
    JSONObject listObject = new JSONObject();
    JSONArray jsonList = new JSONArray();
    JSONPair jsonListPair = new JSONPair("list", jsonList);
    listObject.add(jsonListPair);
    int row = super.getOffset();
    for (Host host : list) {
      JSONObject jsonSite = new JSONObject();
      jsonSite.add(new JSONPair("row", row));
      jsonSite.add(new JSONPair("id", host.getId()));
      jsonSite.add(new JSONPair("name", host.getName()));
      jsonList.add(jsonSite);
      row++;
    }

    return listObject;
  }
}
