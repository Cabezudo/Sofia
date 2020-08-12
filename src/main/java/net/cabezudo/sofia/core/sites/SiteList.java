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

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
// TODO Extends from Sites, this is a paginated list of sites.
public class SiteList extends EntityList<Site> {

  List<Site> list = new ArrayList<>();
  Map<Integer, Site> map = new HashMap<>();

  public SiteList(int offset, int pageSize) {
    super(offset, pageSize);
  }

  @Override
  public Iterator<Site> iterator() {
    return list.iterator();
  }

  public void add(Site s) throws SQLException {
    int id = s.getId();
    Site site = map.get(id);
    if (site == null) {
      list.add(s);
      map.put(id, s);
    } else {
      site = new Site(id, site.getName(), site.getBaseDomainName(), site.getDomainNames(), site.getVersion());
      map.put(id, site);
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
    for (Site site : list) {
      JSONObject jsonSite = new JSONObject();
      jsonSite.add(new JSONPair("row", row));
      jsonSite.add(new JSONPair("id", site.getId()));
      jsonSite.add(new JSONPair("name", site.getName()));
      jsonSite.add(new JSONPair("version", site.getVersion()));
      jsonList.add(jsonSite);
      row++;
    }

    return listObject;
  }
}
