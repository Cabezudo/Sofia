package net.cabezudo.sofia.core.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.logger.Logger;

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
        throw new RuntimeConfigurationException(e);
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

  private static final String ENVIRONMENT_PROPERTY_NAME = "environment";
  private static final String PRODUCTION_ENVIRONMENT = "production";
  private static final String DEVELOPMENT_ENVIRONMENT = "development";
  private static final String DATABASE_DRIVER_PROPERTY_NAME = "database.driver";
  private static final String DATABASE_HOSTNAME_PROPERTY_NAME = "database.hostname";
  private static final String DATABASE_PORT_PROPERTY_NAME = "database.port";
  private static final String DATABASE_NAME_PROPERTY_NAME = "database.name";
  private static final String DATABASE_USERNAME_PROPERTY_NAME = "database.username";
  private static final String DATABASE_PASSWORD_PROPERTY_NAME = "database.password";
  private static final String SERVER_PORT_PROPERTY_NAME = "server.port";
  private static final String SYSTEM_HOME_PROPERTY_NAME = "system.home";

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
  private final Path commonSourcesPath;
  private final Path sitesPath;

  public static void validateConfiguration() throws ConfigurationException {
    Utils.consoleOutLn("Validate environment configuration.");
    if (!DEVELOPMENT_ENVIRONMENT.equals(Configuration.getInstance().environment) && !PRODUCTION_ENVIRONMENT.equals(Configuration.getInstance().environment)) {
      throw new ConfigurationException(
              "Invalid value " + Configuration.getInstance().environment + " for environment. MUST BE " + DEVELOPMENT_ENVIRONMENT + " or " + PRODUCTION_ENVIRONMENT);
    }
    Utils.consoleOut("Test connection.");
    Configuration.getInstance().testConnection();
    Utils.consoleOutLn(" OK");
  }

  private Configuration() throws IOException {
    System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tH:%1$tM:%1$tS.%1$tL [%4$s] %2$s : %5$s%n");
    String path = getConfigurationFilePath();

    File file = new File(path);
    Logger.info("Configuration file FOUND in " + file + ".");
    if (file.exists()) {
      InputStream is = new FileInputStream(file);
      Properties properties = new Properties();
      properties.load(is);

      environment = getProperty(properties, ENVIRONMENT_PROPERTY_NAME);
      databaseDriver = getProperty(properties, DATABASE_DRIVER_PROPERTY_NAME);
      databaseHostname = getProperty(properties, DATABASE_HOSTNAME_PROPERTY_NAME);
      databasePort = getProperty(properties, DATABASE_PORT_PROPERTY_NAME);
      databaseName = getProperty(properties, DATABASE_NAME_PROPERTY_NAME);
      databaseUsername = getProperty(properties, DATABASE_USERNAME_PROPERTY_NAME);
      databasePassword = getProperty(properties, DATABASE_PASSWORD_PROPERTY_NAME);
      serverPort = Integer.parseInt(getProperty(properties, SERVER_PORT_PROPERTY_NAME));
      String sofiaBasePath = getProperty(properties, SYSTEM_HOME_PROPERTY_NAME);
      systemPath = Paths.get(sofiaBasePath).resolve("system");
      checkPath(systemPath);
      systemDataPath = systemPath.resolve("data");
      Files.createDirectories(systemDataPath);
      commonSourcesPath = systemPath.resolve("sources");
      Files.createDirectories(commonSourcesPath);
      sitesDataPath = commonSourcesPath.resolve("data");
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
      sitesSourcesPath = commonSourcesPath.resolve("sites");
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
      throw new RuntimeConfigurationException("Property not found: " + propertyName);
    }
    if (!DATABASE_PASSWORD_PROPERTY_NAME.equals(propertyName)) {
      Logger.info("%s: %s", propertyName, propertyValue);
    }
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
    return StandardCharsets.UTF_8;
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

  public Path getCommonSourcesPath() {
    return commonSourcesPath;
  }

  public Path getSitesPath() {
    return sitesPath;
  }

  public Path getCommonsTemplatesPath() {
    return commonsTemplatesPath;
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
    switch (name) {
      case "database.backup.file.name":
        return "/home/esteban/NetBeansProjects/core.cabezudo.net/system/data/cabezudo.sql";
      case "mail.smtp.host":
        return "in.mailjet.com";
      case "mail.server.mailJet.api.key":
        return "c1a2afcc6be16031275520db9ec5e79b";
      case "mail.server.mailJet.secret.key":
        return "f30eb6ff628984df69a6ce3000a8d912";
      default:
        throw new RuntimeConfigurationException("Configuration value not found: " + name);
    }
  }

  private void checkPath(Path path) {
    if (!Files.isDirectory(path)) {
      throw new RuntimeConfigurationException("The file " + path + " IS NOT a directory.");
    }
  }

  public static String getConfigurationFilePath() {
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

  public static Path createFile() throws ConfigurationException {
    String directoryName = System.getProperty("user.home");
    Path basePath = Paths.get(directoryName);
    Path filePath = basePath.resolve(CONFIGURATION_FILENAME);
    if (Files.exists(filePath)) {
      throw new RuntimeConfigurationException("The file " + filePath + " already exists.");
    }
    if (Files.isWritable(basePath)) {
      try {
        Files.createFile(filePath);
      } catch (IOException e) {
        throw new ConfigurationException("Can't create the file. " + e.getMessage());
      }
      try {
        try ( FileWriter out = new FileWriter(filePath.toFile())) {
          out.write("environment=production\n");
          out.write("database.driver=com.mysql.cj.jdbc.Driver\n");
          out.write("database.hostname=localhost\n");
          out.write("database.port=3306\n");
          out.write("database.name=sofia\n");
          out.write("database.username=santiago\n");
          out.write("database.password=nasar1930\n");
          out.write("server.port=80\n");
          out.write("system.home=/home/sofia\n");
        }
      } catch (IOException e) {
        throw new ConfigurationException("Can't write on the file. " + e.getMessage());
      }
      return filePath;
    }
    throw new ConfigurationException("The " + basePath + " is not writable.");
  }

  public String getLoginURL() {
    return "/login.html";
  }

  public Path getAPIConfigurationFile() {
    return systemPath.resolve("apiDefinition.json");
  }

  private void testConnection() throws ConfigurationException {
    try {
      Database.getConnection(null, 1);
    } catch (SQLException e) {
      throw new ConfigurationException("Invalid configuration for database. " + e.getMessage(), e);
    }
  }
}
