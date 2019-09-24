package net.cabezudo.sofia.core.sites;

import static java.lang.Integer.compare;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.sql.SQLException;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.emails.EMail;
import net.cabezudo.sofia.hosts.Host;
import net.cabezudo.sofia.hosts.HostList;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.23
 */
public class Site implements Comparable<Integer> {

  public final static int NAME_MAX_LENGTH = 80;
  private final Integer id;
  private final String name;
  private final HostList hosts;
  private final int version;

  Site(int id, String name, int version) {
    this.id = id;
    this.name = name;
    this.hosts = new HostList();
    this.version = version;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getVersion() {
    return version;
  }

  public void addHost(int hostId, String hostName) {
    Host host = new Host(hostId, hostName);
    this.add(host);
  }

  public void add(Host host) {
    hosts.add(host);
  }

  public void setBaseHost(Host host) {
    hosts.setBaseHost(host);
  }

  public void setBaseHost(int baseHostId) {
    hosts.setBaseHost(baseHostId);
  }

  public HostList getHosts() throws SQLException {
    return hosts;
  }

  public Host getBaseHost() {
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

  @Override
  public int compareTo(Integer anotherInteger) {
    return compare(this.id, anotherInteger);
  }

  public URI getURL() {
    try {
      URI uri = new URI(this.getBaseHost().getName());
      return uri;
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  // TODO Get this value from a site configuration file
  public URI getPasswordChangeURI() throws URISyntaxException {
    return new URI("https://" + this.getBaseHost().getName() + "/changePassword.html");
  }

  public String getNoReplyName() {
    // TODO Get this from de site configuration
    return "No reply";
  }

  public EMail getNoReplyEMail() {
    // TODO Get this from de site configuration
//    return new EMail(0, 0, "no-reply@" + this.getBaseHost().getName());
    return new EMail(0, 0, "esteban@cabezudo.net");
  }

  public long getPasswordChangeHashExpireTime() {
    // TODO Get this from de site configuration
    return 7200; // Dos horas
  }

  public String toJSON() {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append("\"id\": ");
    sb.append(id);
    sb.append(", ");
    sb.append("\"name\": \"");
    sb.append(name);
    sb.append("\", ");
    sb.append("\"hosts\": ");
    sb.append(hosts.toJSON());
    sb.append(", ");
    sb.append("\"version\": ");
    sb.append(version);
    sb.append("}");
    return sb.toString();
  }
}
