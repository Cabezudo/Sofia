package net.cabezudo.sofia.core.sites;

import static java.lang.Integer.compare;
import java.nio.file.Path;
import java.sql.SQLException;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.domains.DomainName;
import net.cabezudo.sofia.domains.DomainNameList;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.23
 */
public class Site implements Comparable<Integer> {

  public final static int NAME_MAX_LENGTH = 80;
  private final Integer id;
  private final String name;
  private final DomainNameList hosts;
  private final int version;

  Site(int id, String name, int version) {
    this.id = id;
    this.name = name;
    this.hosts = new DomainNameList();
    this.version = version;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void addHost(int hostId, String hostName) {
    DomainName domainName = new DomainName(hostId, hostName);
    this.add(domainName);
  }

  public void add(DomainName domainName) {
    hosts.add(domainName);
  }

  public void setBaseHost(DomainName domainName) {
    hosts.setBaseHost(domainName);
  }

  public void setBaseHost(int baseHostId) {
    hosts.setBaseHost(baseHostId);
  }

  public DomainNameList getHosts() throws SQLException {
    return hosts;
  }

  public DomainName getBaseHost() {
    return hosts.getBaseHost();
  }

  @Override
  public String toString() {
    return "[id: " + id + ", name: " + name + ", hosts: " + hosts + ", version: " + version + " ]";
  }

  public Path getBasePath() {
    return Configuration.getInstance().getSitesPath().resolve(getBaseHost().getName());
  }

  public Path getVersionPath() {
    return this.getBasePath().resolve(Integer.toString(version));
  }

  public Path getCSSPath() {
    return getVersionPath().resolve("css");
  }

  public Path getJSPath() {
    return getVersionPath().resolve("js");
  }

  public Path getImagesPath() {
    return getVersionPath().resolve("images");
  }

  public Path getSourcesPath() {
    return Configuration.getInstance().getSitesSourcesPath().resolve(getBaseHost().getName()).resolve(Integer.toString(version));
  }

  public Path getSourcesImagesPath() {
    return getSourcesPath().resolve("images");
  }

  public Path getThemePath() {
    return Configuration.getInstance().getCommonsThemesPath().resolve(getBaseHost().getName());
  }

  @Override
  public int compareTo(Integer anotherInteger) {
    return compare(this.id, anotherInteger);
  }
}
