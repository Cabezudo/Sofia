package net.cabezudo.sofia.core.sites;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.EntityList;
import net.cabezudo.sofia.core.sites.domainname.DomainName;
import net.cabezudo.sofia.core.sites.domainname.DomainNameList;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
// TODO Extends from Sites, this is a paginated list of sites.
public class SiteList extends EntityList<Site> {

  Map<Integer, RawSite> rawMap = new HashMap<>();
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

  void add(int siteId, String siteName, int baseDomainNameId, int version, int domainNameId, String domainNameName) {
    RawSite rawSite = rawMap.computeIfAbsent(siteId, key -> new RawSite(key, siteName, baseDomainNameId, domainNameId, domainNameName, version));
    DomainName domainName = new DomainName(domainNameId, siteId, domainNameName);
    rawSite.domainNameList.add(domainName);
  }

  // Create the list from the raw map
  void create() throws SQLException {
    for (Entry<Integer, RawSite> entry : rawMap.entrySet()) {
      RawSite rawSite = entry.getValue();
      Site site = new Site(rawSite.id, rawSite.name, rawSite.baseDomainName, rawSite.domainNameList, rawSite.version);
      add(site);
    }
  }

  private static class RawSite {

    private final int id;
    private final String name;
    private DomainName baseDomainName;
    private final DomainNameList domainNameList = new DomainNameList();
    private final int version;

    private RawSite(int id, String name, int baseDomainNameId, int domainNameId, String domainNameName, int version) {
      this.id = id;
      this.name = name;
      DomainName domainName = new DomainName(domainNameId, id, domainNameName);
      if (domainNameId == baseDomainNameId) {
        this.baseDomainName = domainName;
      }
      domainNameList.add(domainName);
      this.version = version;
    }
  }
}
