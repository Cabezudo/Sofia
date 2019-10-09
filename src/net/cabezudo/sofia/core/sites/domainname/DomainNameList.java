package net.cabezudo.sofia.core.sites.domainname;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.cabezudo.sofia.core.Utils;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class DomainNameList implements Iterable<DomainName> {

  Map<Integer, DomainName> map = new TreeMap<>();
  List<DomainName> list = new ArrayList<>();
  private final int offset;
  private final int size;
  private int total;

  public DomainNameList(int offset, int size) {
    this.offset = offset;
    this.size = size;
  }

  public DomainNameList() {
    this(0, 0);
  }

  public DomainName[] toArray() {
    DomainName array[] = new DomainName[list.size()];
    return list.toArray(array);
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

  public String toJSON() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (DomainName domainName : list) {
      sb.append(domainName.toJSON());
      sb.append(", ");
    }
    if (!list.isEmpty()) {
      sb = Utils.chop(sb, 2);
    }
    sb.append("]");
    return sb.toString();
  }

  public String[] toStringArray() {
    String[] names = new String[list.size()];
    int i = 0;
    for (DomainName domainName : list) {
      names[i] = domainName.getName();
      i++;
    }
    return names;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public void add(DomainNameList domainNames) {
    for (DomainName domainName : domainNames) {
      add(domainName);
    }
  }
}
