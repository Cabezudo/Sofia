package net.cabezudo.sofia.core.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import net.cabezudo.sofia.core.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.24
 */
public final class Configuration {

  private static Configuration INSTANCE;

  public static Configuration getInstance() {
    if (INSTANCE == null) {
      try {
        INSTANCE = new Configuration();
      } catch (IOException e) {
        throw new ConfigurationException(e);
      }
    }
    return INSTANCE;
  }

  public static void searchFile() throws ConfigurationFileNotFoundException {
    String configurationFilePath = getConfigurationFilePath();
    if (configurationFilePath == null) {
      throw new ConfigurationFileNotFoundException("Configuration file not found.");
    }
  }

  private static final String CONFIGURATION_FILENAME = "sofia.configuration.properties";

  private final String environment;
  private final String databaseDriver;
  private final String databaseHostname;
  private final String databasePort;
  private final String databaseName;
  private final String databaseUsername;
  private final String databasePassword;
  private final int serverPort;
  private final Path systemPath;
  private final Path systemDataPath;
  private final Path sitesDataPath;
  private final Path commonsFontsPath;
  private final Path commonsLibsPath;
  private final Path commonsTemplatesPath;
  private final Path commonsComponentsTemplatesPath;
  private final Path commonsThemesPath;
  private final Path commonsMailTemplatesPath;
  private final Path sitesSourcesPath;
  private final Path sourcesPath;
  private final Path sitesPath;

  private Configuration() throws FileNotFoundException, IOException {
    System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tH:%1$tM:%1$tS.%1$tL [%4$s] %2$s : %5$s%n");
    String path = getConfigurationFilePath();

    File file = new File(path);
    Logger.info("Configuration file FOUND in " + file + ".");
    if (file.exists()) {
      InputStream is = new FileInputStream(file);
      Properties properties = new Properties();
      properties.load(is);

      environment = getProperty(properties, "environment");
      databaseDriver = getProperty(properties, "database.driver");
      databaseHostname = getProperty(properties, "database.hostname");
      databasePort = getProperty(properties, "database.port");
      databaseName = getProperty(properties, "database.name");
      databaseUsername = getProperty(properties, "database.username");
      databasePassword = getProperty(properties, "database.password");
      serverPort = Integer.parseInt(getProperty(properties, "server.port"));
      String sofiaBasePath = getProperty(properties, "system.home");
      if (sofiaBasePath == null || sofiaBasePath.isEmpty()) {
        systemPath = Paths.get(System.getProperty("user.home")).resolve("system");
      } else {
        systemPath = Paths.get(sofiaBasePath).resolve("system");
      }
      checkPath(systemPath);
      systemDataPath = systemPath.resolve("data");
      Files.createDirectories(systemDataPath);
      sourcesPath = systemPath.resolve("sources");
      Files.createDirectories(sourcesPath);
      sitesDataPath = sourcesPath.resolve("data");
      Files.createDirectories(sitesDataPath);
      commonsFontsPath = sitesDataPath.resolve("fonts");
      Files.createDirectories(commonsFontsPath);
      commonsLibsPath = sitesDataPath.resolve("libs");
      Files.createDirectories(commonsLibsPath);
      commonsTemplatesPath = sitesDataPath.resolve("templates");
      Files.createDirectories(commonsTemplatesPath);
      commonsComponentsTemplatesPath = commonsTemplatesPath.resolve("components");
      Files.createDirectories(commonsComponentsTemplatesPath);
      commonsMailTemplatesPath = commonsTemplatesPath.resolve("mail");
      Files.createDirectories(commonsMailTemplatesPath);
      commonsThemesPath = sitesDataPath.resolve("themes");
      Files.createDirectories(commonsThemesPath);
      sitesSourcesPath = sourcesPath.resolve("sites");
      Files.createDirectories(sitesSourcesPath);
      sitesPath = systemPath.resolve("sites");
      Files.createDirectories(sitesPath);
    } else {
      throw new FileNotFoundException(file.getAbsolutePath());
    }
  }

  private String getProperty(Properties properties, String propertyName) {
    String propertyValue = properties.getProperty(propertyName);
    if (propertyValue == null) {
      throw new ConfigurationException("Property not found: " + propertyName);
    }
    Logger.info("%s: %s", propertyName, propertyValue);
    return propertyValue;
  }

  public String get(String name) {
    String value = getDatabaseValue(name);
    Logger.debug("%s: %s", name, value);
    return value;
  }

  public int getInteger(String name) {
    return Integer.parseInt(get(name));
  }

  public Charset getEncoding() {
    return Charset.forName("UTF-8");
  }

  public String getEnvironmentName() {
    return environment;
  }

  public String getDatabaseDriver() {
    return databaseDriver;
  }

  public String getDatabaseHostname() {
    return databaseHostname;
  }

  public String getDatabasePort() {
    return databasePort;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public String getDatabaseUser() {
    return databaseUsername;
  }

  public String getDatabasePassword() {
    return databasePassword;
  }

  public int getServerPort() {
    return serverPort;
  }

  public Path getSystemPath() {
    return systemPath;
  }

  public Path getSystemDataPath() {
    return systemDataPath;
  }

  public Path getCommonsFontsPath() {
    return commonsFontsPath;
  }

  public Path getCommonsLibsPath() {
    return commonsLibsPath;
  }

  public Path getCommonsTemplatesPath() {
    return commonsTemplatesPath;
  }

  public Path getCommonsComponentsTemplatesPath() {
    return commonsComponentsTemplatesPath;
  }

  public Path getCommonsThemesPath() {
    return commonsThemesPath;
  }

  public Path getCommonsMailTemplatesPath() {
    return commonsMailTemplatesPath;
  }

  public Path getSitesSourcesPath() {
    return sitesSourcesPath;
  }

  public Path getSourcesPath() {
    return sourcesPath;
  }

  public Path getSitesPath() {
    return sitesPath;
  }

  public Path getSitesDataPath() {
    return sitesDataPath;
  }

  public String getEMailPasswordRecoveryTemplateName() {
    return "eMailPasswordRecovery";
  }

  public String getEMailPasswordChangedTemplateName() {
    return "eMailPasswordChanged";
  }

  public String getEMailRegistrationRetryAlertTemplateName() {
    return "eMailRegistrationRetryAlert";
  }

  // TODO use the database for configuration
  private String getDatabaseValue(String name) {
    // TODO Leer todo esto de la base de datos
    switch (name) {
      case "database.backup.file.name":
        return "/home/esteban/NetBeansProjects/core.cabezudo.net/system/data/cabezudo.sql";
      case "site.name":
        return "Menu CDMX";
      case "site.domain.name":
        return "localhost";
      case "site.uri":
        return "http://" + getDatabaseValue("site.domain.name");
      case "password.change.uri":
        return getDatabaseValue("site.uri") + "/setPassword.jsp";
      case "password.change.hash.time":
        return "120";
      case "password.recover.page.url":
        return getDatabaseValue("site.uri") + "/passwordRecover.jsp";
      case "mail.smtp.host":
        return "in.mailjet.com";
      case "mail.server.mailJet.api.key":
        return "c1a2afcc6be16031275520db9ec5e79b";
      case "mail.server.mailJet.secret.key":
        return "f30eb6ff628984df69a6ce3000a8d912";
      case "no.reply.name":
        return get("site.name");
      case "no.reply.email":
        return "esteban@cabezudo.net";
      default:
        throw new ConfigurationException("Configuration value not found: " + name);
    }
  }

  private void checkPath(Path path) {
    if (!Files.isDirectory(path)) {
      throw new ConfigurationException("The file " + path + " is not a directory.");
    }
  }

  private static String getConfigurationFilePath() {
    String path;
    path = searchOn(System.getProperty("user.dir") + System.getProperty("file.separator") + CONFIGURATION_FILENAME);
    if (path != null) {
      return path;
    }
    path = searchOn(System.getProperty("user.home") + System.getProperty("file.separator") + CONFIGURATION_FILENAME);
    if (path != null) {
      return path;
    }
    return null;
  }

  private static String searchOn(String path) {
    Logger.info("Check for configuration file in " + path + ".");
    if (Files.exists(Paths.get(path))) {
      return path;
    }
    return null;
  }

  public String getLoginURL() {
    return "/login.html";
  }
}
