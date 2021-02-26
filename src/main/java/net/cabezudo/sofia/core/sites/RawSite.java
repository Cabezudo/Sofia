package net.cabezudo.sofia.core.sites;

import java.nio.file.Path;
import net.cabezudo.sofia.core.sites.domainname.DomainName;
import net.cabezudo.sofia.core.sites.domainname.DomainNameList;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.23
 */
class RawSite {

  private final int id;
  private final String name;
  private final Path basePath;
  private DomainName baseDomainName;
  private final DomainNameList domainNameList = new DomainNameList();
  private final int version;

  RawSite(int id, String name, Path basePath, int version) {
    this.id = id;
    this.name = name;
    this.basePath = basePath;
    this.version = version;
  }

  int getId() {
    return id;
  }

  String getName() {
    return name;
  }

  Path getBasePath() {
    return basePath;
  }

  DomainName getBaseDomainName() {
    return baseDomainName;
  }

  DomainNameList getDomainNameList() {
    return domainNameList;
  }

  int getVersion() {
    return version;
  }

  void add(int baseDomainNameId, DomainName domainName) {
    domainNameList.add(domainName);
    if (domainName.getId() == baseDomainNameId) {
      this.baseDomainName = domainName;
    }
  }
}
