package net.cabezudo.sofia.hosts;

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
public class HostList implements Iterable<Host> {

  Map<Integer, Host> map = new TreeMap<>();
  List<Host> list = new ArrayList<>();
  private int baseHostId;

  public Host[] toArray() {
    Host array[] = new Host[list.size()];
    return list.toArray(array);
  }

  public String[] toStringArray() {
    String array[] = new String[list.size()];
    int i = 0;
    for (Host host : list) {
      array[i] = host.getName();
      i++;
    }
    return array;
  }

  public void add(Host host) {
    if (host == null) {
      throw new NullPointerException("domainName is null");
    }
    map.put(host.getId(), host);
    list.add(host);
  }

  @Override
  public Iterator<Host> iterator() {
    return list.iterator();
  }

  public Host getBaseHost() {
    return map.get(baseHostId);
  }

  public void setBaseHost(Host host) {
    this.baseHostId = host.getId();
  }

  public void setBaseHost(int baseHostId) {
    this.baseHostId = baseHostId;
  }

  public String toJSON() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (Host host : list) {
      sb.append(host.toJSON());
      sb.append(", ");
    }
    if (!list.isEmpty()) {
      sb = Utils.chop(sb, 2);
    }
    sb.append("]");
    return sb.toString();
  }
}
