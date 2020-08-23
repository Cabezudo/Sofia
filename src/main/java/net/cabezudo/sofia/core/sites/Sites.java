package net.cabezudo.sofia.core.sites;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.cabezudo.sofia.core.sites.domainname.DomainName;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.07
 */
public class Sites implements Iterable<Site> {

  private Map<Integer, RawSite> rawMap = new HashMap<>();
  private final Map<Integer, Site> map = new TreeMap<>();
  private final List<Site> list = new ArrayList<>();

  public int size() {
    return list.size();
  }

  public Site getById(int id) {
    return map.get(id);
  }

  @Override
  public Iterator<Site> iterator() {
    return list.iterator();
  }

  public void add(Site site) {
    map.put(site.getId(), site);
    list.add(site);
  }

  void add(int siteId, String siteName, int baseDomainNameId, int version, int domainNameId, String domainNameName) {
    RawSite rawSite = rawMap.computeIfAbsent(siteId, key -> new RawSite(key, siteName, version));
    DomainName domainName = new DomainName(domainNameId, siteId, domainNameName);
    rawSite.add(baseDomainNameId, domainName);
  }

  // Create the list from the raw map
  void create() throws SQLException {
    for (Map.Entry<Integer, RawSite> entry : rawMap.entrySet()) {
      RawSite rawSite = entry.getValue();
      Site site = new Site(rawSite);
      add(site);
    }
  }
}
