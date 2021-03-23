package net.cabezudo.sofia.core.sites;

import static java.lang.Integer.compare;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.configuration.ConfigurationException;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.sites.domainname.DomainName;
import net.cabezudo.sofia.core.sites.domainname.DomainNameList;
import net.cabezudo.sofia.emails.EMail;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.23
 */
public class Site implements Comparable<Integer> {

  public static final String COMMONS_FILE_NAME = "commons.json";
  public static final String TEXTS_FILE_NAME = "texts.json";
  public static final int NAME_MAX_LENGTH = 80;

  private final Integer id;
  private final String name;
  private final Path basePath;
  private final DomainName baseDomainName;
  private final DomainNameList domainNames;
  private final int version;

  public Site(int id, String name, Path basePath, DomainName baseDomainName, DomainNameList domainNameList, int version) {
    this.id = id;
    this.name = name;
    this.basePath = basePath;
    this.domainNames = domainNameList;
    this.baseDomainName = baseDomainName;
    this.version = version;
    checkSiteData();
  }

  Site(RawSite rawSite) {
    this(rawSite.getId(), rawSite.getName(), rawSite.getBasePath(), rawSite.getBaseDomainName(), rawSite.getDomainNameList(), rawSite.getVersion());
  }

  Site(ResultSet rs) throws SQLException {
    int siteId;
    String siteName;
    Path siteBasePath;
    int baseDomainNameId;
    int domainNameId;
    String domainNameName;
    int siteVersion;

    RawSite rawSite = null;
    do {
      siteId = rs.getInt("siteId");
      siteName = rs.getString("siteName");
      siteBasePath = Paths.get(rs.getString("siteBasePath"));
      baseDomainNameId = rs.getInt("baseDomainNameId");
      domainNameId = rs.getInt("domainNameId");
      domainNameName = rs.getString("domainNameName");
      siteVersion = rs.getInt("siteVersion");
      if (rawSite == null) {
        rawSite = new RawSite(siteId, siteName, siteBasePath, siteVersion);
      }
      DomainName domainName = new DomainName(domainNameId, siteId, domainNameName);
      rawSite.add(baseDomainNameId, domainName);
    } while (rs.next());

    this.id = siteId;
    this.name = siteName;
    this.basePath = siteBasePath;
    this.domainNames = rawSite.getDomainNameList();
    this.baseDomainName = rawSite.getBaseDomainName();
    this.version = siteVersion;
    checkSiteData();
  }

  private void checkSiteData() {
    if (baseDomainName == null) {
      throw new SofiaRuntimeException("The base domain name is null");
    }
    if (domainNames == null || domainNames.isEmpty()) {
      throw new SofiaRuntimeException("The domain name list is null");
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
    return "[id: " + id + ", name: " + name + ", basePath: " + basePath + ", domainName: " + baseDomainName + ", version: " + version + " ]";
  }

  Path getBasePath(Path basePath) {
    return Configuration.getInstance().getSitesPath().resolve(basePath);
  }

  public Path getBasePath() {
    return getBasePath(basePath);
  }

  public Path getVersionPath() {
    return this.getBasePath().resolve(Integer.toString(version));
  }

  public Path getFilesPath(Path partialPath) {
    Path fileName = partialPath.getFileName();
    Path parentPath = partialPath.getParent();
    Path basePath;
    if (parentPath == null) {
      basePath = getVersionPath();
    } else {
      basePath = getVersionPath().resolve(parentPath);
    }
    return basePath.resolve(fileName);
  }

  public Path getSourcesPath(Path basePath) {
    return Configuration.getInstance().getSitesSourcesPath().resolve(basePath);
  }

  public Path getVersionedSourcesPath() {
    return Site.this.getVersionedSourcesPath(basePath, version);
  }

  public Path getVersionedSourcesPath(Path basePath) {
    return Site.this.getVersionedSourcesPath(basePath, version);
  }

  public Path getVersionedSourcesPath(Path basePath, int version) {
    return getSourcesPath(basePath).resolve(Integer.toString(version));
  }

  public Path getVersionedSourcesCommonsFilePath() {
    return getVersionedSourcesPath().resolve(COMMONS_FILE_NAME);
  }

  public Path getSourcesImagesPath() {
    return Site.this.getVersionedSourcesPath().resolve("images");
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
      throw new SofiaRuntimeException(e);
    }
  }

  // TODO Get this value from a site configuration file
  public URI getPasswordChangeURI() throws ConfigurationException {
    try {
      return new URI("https://" + baseDomainName.getName() + "/changePassword.html");
    } catch (URISyntaxException e) {
      throw new ConfigurationException(e);
    }
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
    sb.append("\"basePath\": \"");
    sb.append(basePath);
    sb.append("\", ");
    sb.append("\"domains\": {");
    DomainNameList list = new DomainNameList();
    list.add(domainNames);
    sb.append("\"base\": ").append(this.getBaseDomainName().toJSON()).append(", ");
    sb.append("\"list\": ").append(list.toJSON());
    sb.append("}, ");
    sb.append("\"version\": ");
    sb.append(version);
    sb.append("}");
    return sb.toString();
  }
}
