package net.cabezudo.sofia.core.sites;

import static java.lang.Integer.compare;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.sql.SQLException;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.domainName.DomainName;
import net.cabezudo.sofia.domainName.DomainNameList;
import net.cabezudo.sofia.emails.EMail;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.23
 */
public class Site implements Comparable<Integer> {

  public final static int NAME_MAX_LENGTH = 80;
  private final Integer id;
  private final String name;
  private final DomainName baseDomainName;
  private final DomainNameList domainNames;
  private final int version;

  Site(int id, String name, DomainName baseDomainName, DomainNameList domainNameList, int version) {
    this.id = id;
    this.name = name;
    this.domainNames = domainNameList;
    this.baseDomainName = baseDomainName;
    this.version = version;
    if (baseDomainName == null) {
      throw new RuntimeException("The base domain name is null");
    }
    if (domainNames == null) {
      throw new RuntimeException("The domain name list is null");
    }
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public DomainName getBaseDomainName() {
    return baseDomainName;
  }

  public int getVersion() {
    return version;
  }

  public void _addDomainName(int domainNameId, String domainNameName) {
    if (domainNameId == baseDomainName.getId()) {
      this.add(baseDomainName);
    } else {
      DomainName newDomainName = new DomainName(domainNameId, id, domainNameName);
      this.add(newDomainName);
    }
  }

  private void add(DomainName domainName) {
    domainNames.add(domainName);
  }

  public DomainNameList getDomainNames() throws SQLException {
    return domainNames;
  }

  @Override
  public String toString() {
    return "[id: " + id + ", name: " + name + ", domainName: " + baseDomainName + ", version: " + version + " ]";
  }

  public Path getBasePath() {
    System.out.println(baseDomainName);
    return Configuration.getInstance().getSitesPath().resolve(baseDomainName.getName());
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
    return Configuration.getInstance().getSitesSourcesPath().resolve(baseDomainName.getName()).resolve(Integer.toString(version));
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
      URI uri = new URI(baseDomainName.getName());
      return uri;
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  // TODO Get this value from a site configuration file
  public URI getPasswordChangeURI() throws URISyntaxException {
    return new URI("https://" + baseDomainName.getName() + "/changePassword.html");
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
    sb.append(domainNames.toJSON());
    sb.append(", ");
    sb.append("\"version\": ");
    sb.append(version);
    sb.append("}");
    return sb.toString();
  }
}
