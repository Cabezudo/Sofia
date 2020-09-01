package net.cabezudo.sofia.core.sites.domainname;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.31
 */
public class DomainNames implements Iterable<DomainName> {

  List<DomainName> list = new ArrayList<>();
  Map<Integer, DomainName> map = new TreeMap<>();

  DomainName[] toArray() {
    DomainName[] array = new DomainName[list.size()];
    return list.toArray(array);
  }

  public void add(DomainName domainName) {
    if (domainName == null) {
      throw new NullPointerException("null parameter");
    }
    map.put(domainName.getId(), domainName);
    list.add(domainName);
  }

  @Override
  public Iterator<DomainName> iterator() {
    return list.iterator();
  }

  boolean isEmpty() {
    return list.isEmpty();
  }

  int size() {
    return list.size();
  }

}
