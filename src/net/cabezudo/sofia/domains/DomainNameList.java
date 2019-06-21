package net.cabezudo.sofia.domains;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class DomainNameList implements Iterable<DomainName> {

  Map<Integer, DomainName> map = new TreeMap<>();
  List<DomainName> list = new ArrayList<>();
  private int baseHostId;

  public DomainName[] toArray() {
    DomainName array[] = new DomainName[list.size()];
    return list.toArray(array);
  }

  public String[] toStringArray() {
    String array[] = new String[list.size()];
    int i = 0;
    for (DomainName domainName : list) {
      array[i] = domainName.getName();
      i++;
    }
    return array;
  }

  public void add(DomainName domainName) {
    if (domainName == null) {
      throw new NullPointerException("domainName is null");
    }
    map.put(domainName.getId(), domainName);
    list.add(domainName);
  }

  @Override
  public Iterator<DomainName> iterator() {
    return list.iterator();
  }

  public DomainName getBaseHost() {
    return map.get(baseHostId);
  }

  public void setBaseHost(DomainName domainName) {
    this.baseHostId = domainName.getId();
  }

  public void setBaseHost(int baseHostId) {
    this.baseHostId = baseHostId;
  }
}
