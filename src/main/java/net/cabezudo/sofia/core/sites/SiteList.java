package net.cabezudo.sofia.core.sites;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Iterator;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.list.EntryList;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
// TODO Extends from Sites, this is a paginated list of sites.
public class SiteList extends EntryList<Site> {

  Sites sites = new Sites();

  public SiteList(int offset, int pageSize) {
    super(offset, pageSize);
  }

  @Override
  public Iterator<Site> iterator() {
    return sites.iterator();
  }

  public void add(Site s) throws SQLException {
    sites.add(s);
  }

  @Override
  public JSONValue toJSONTree() {
    JSONObject listObject = new JSONObject();
    JSONArray jsonList = new JSONArray();
    JSONPair jsonListPair = new JSONPair("list", jsonList);
    listObject.add(jsonListPair);
    int row = super.getOffset();
    for (Site site : sites) {
      JSONObject jsonSite = new JSONObject();
      jsonSite.add(new JSONPair("row", row));
      jsonSite.add(new JSONPair("id", site.getId()));
      jsonSite.add(new JSONPair("name", site.getName()));
      jsonSite.add(new JSONPair("basePath", site.getBasePath().toString()));
      jsonSite.add(new JSONPair("version", site.getVersion()));
      jsonList.add(jsonSite);
      row++;
    }

    return listObject;
  }

  void add(int id, String name, Path basePath, int baseDomainNameId, int version, int domainNameId, String domainNameName) {
    sites.add(id, name, basePath, baseDomainNameId, version, domainNameId, domainNameName);
  }

  void create() throws SQLException {
    sites.create();
  }

  @Override
  public void toFormatedString(StringBuilder sb, int indent, boolean includeFirst) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
