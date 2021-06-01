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

  public static final String COMMONS_CONFIGURATION_FILE_NAME = "commons.json";
  public static final String COMMONS_CSS_FILE_NAME = "commons.css";
  public static final String TEXTS_FILE_NAME = "texts.json";
  public static final int NAME_MAX_LENGTH = 80;

  private final Integer id;
  private final String name;
  private final Path basePath;
  private final DomainName baseDomainName;
  private final DomainNameList domainNames;
  private final int version;
  private final Path fullBasePath;
  private final Path versionedSourcesPath;
  private final Path versionedSourcesImagesPath;
  private final Path versionedSourcesFilesPath;
  private final Path versionedSiteBasePath;
  private final Path customVersionedImagesPath;
  private final Path customFilesPath;

  public Site(int id, String name, Path basePath, DomainName baseDomainName, DomainNameList domainNameList, int version) {
    this.id = id;
    this.name = name;
    this.basePath = basePath;
    this.domainNames = domainNameList;
    this.baseDomainName = baseDomainName;
    this.version = version;
    checkSiteData();

    fullBasePath = Configuration.getInstance().getSitesPath().resolve(basePath);
    versionedSourcesPath = getVersionedSourcesPath(basePath, version);
    versionedSourcesImagesPath = versionedSourcesPath.resolve("images");
    versionedSourcesFilesPath = versionedSourcesPath.resolve("files");
    versionedSiteBasePath = fullBasePath.resolve(Integer.toString(version));
    customVersionedImagesPath = versionedSiteBasePath.resolve("images");
    customFilesPath = fullBasePath.resolve("files");
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

    id = rawSite.getId();
    name = rawSite.getName();
    basePath = rawSite.getBasePath();
    this.domainNames = rawSite.getDomainNameList();
    this.baseDomainName = rawSite.getBaseDomainName();
    version = rawSite.getVersion();

    fullBasePath = Configuration.getInstance().getSitesPath().resolve(basePath);
    versionedSourcesPath = getVersionedSourcesPath(basePath, version);
    versionedSourcesImagesPath = versionedSourcesPath.resolve("images");
    versionedSourcesFilesPath = versionedSourcesPath.resolve("files");
    versionedSiteBasePath = fullBasePath.resolve(Integer.toString(version));
    customVersionedImagesPath = versionedSiteBasePath.resolve("images");
    customFilesPath = fullBasePath.resolve("files");
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

  public DomainNameList getDomainNamesList() throws SQLException {
    return domainNames;
  }

  @Override
  public String toString() {
    return "[id: " + id + ", name: " + name + ", basePath: " + basePath + ", domainName: " + baseDomainName + ", version: " + version + " ]";
  }

  public Path getBasePath() {
    return basePath;
  }

  Path getFullBasePath(Path basePath) {
    return Configuration.getInstance().getSitesPath().resolve(basePath);
  }

  public Path getFullBasePath() {
    return fullBasePath;
  }

  public Path getVersionedBasePath() {
    return versionedSiteBasePath;
  }

  public Path getCustomVersionedImagesPath() {
    return customVersionedImagesPath;
  }

  public Path getCustomFilesPath() {
    return customFilesPath;
  }

  public Path getCreatedFilesPath(Path partialPath) {
    Path fileName = partialPath.getFileName();
    Path parentPath = partialPath.getParent();
    Path siteBasePath;
    if (parentPath == null) {
      siteBasePath = getVersionedBasePath();
    } else {
      siteBasePath = getVersionedBasePath().resolve(parentPath);
    }
    return siteBasePath.resolve(fileName);
  }

  public Path getVersionedSourcesFilesPath() {
    return versionedSourcesFilesPath;
  }

  public Path getSourcesPath(Path basePath) {
    return Configuration.getInstance().getSitesSourcesPath().resolve(basePath);
  }

  public Path getVersionedSourcesPath() {
    return versionedSourcesPath;
  }

  public Path getVersionedSourcesPath(Path basePath) {
    return Site.this.getVersionedSourcesPath(basePath, version);
  }

  public final Path getVersionedSourcesPath(Path basePath, int version) {
    return getSourcesPath(basePath).resolve(Integer.toString(version));
  }

  public Path getVersionedSourcesCommonsConfigurationFilePath() {
    return getVersionedSourcesPath().resolve(COMMONS_CONFIGURATION_FILE_NAME);
  }

  public Path getVersionedSourcesImagesPath() {
    return versionedSourcesImagesPath;
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
    return new EMail(0, 0, "test@sofia.academy");
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
