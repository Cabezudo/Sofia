package net.cabezudo.sofia.core.sites;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class SiteList implements Iterable<Site> {

  List<Site> list = new ArrayList<>();

  void add(Site site) {
    list.add(site);
  }

  @Override
  public Iterator<Site> iterator() {
    return list.iterator();
  }
}
