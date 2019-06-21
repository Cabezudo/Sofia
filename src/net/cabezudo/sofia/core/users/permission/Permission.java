package net.cabezudo.sofia.core.users.permission;

import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.16
 */
public class Permission {

  private final int id;
  private final String uri;
  private final Site site;

  public Permission(int id, String uri, Site site) {
    this.id = id;
    this.uri = uri;
    this.site = site;
  }

  public int getId() {
    return id;
  }

  public String getUri() {
    return uri;
  }

  public Site getSite() {
    return site;
  }
}
