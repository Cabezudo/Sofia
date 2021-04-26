package net.cabezudo.sofia.core.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.sofia.core.APIConfiguration;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.database.sql.DatabaseCreators;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.24
 */
public final class Configuration {

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

  private static final Configuration INSTANCE = new Configuration();

  public static Path getStatesDataPath() {
    return INSTANCE.statesDataPath;
  }

  public static Path getCitiesDataPath() {
    return INSTANCE.citiesDataPath;
  }

  public static Path getPostalCodesDataPath() {
    return INSTANCE.postalCodesDataPath;
  }

  private String sofiaBasePath;
  private String environment;
  private String databaseDriver;
  private String databaseHostname;
  private String databasePort;
  private String databaseName;
  private String databaseUsername;
  private String databasePassword;
  private int serverPort;
  private Path systemPath;
  private Path systemResourcesPath;
  private Path systemDataPath;
  private Path statesDataPath;
  private Path citiesDataPath;
  private Path postalCodesDataPath;
  private Path systemImagesPath;
  private Path systemLibsPath;
  private Path sitesDataPath;
  private Path commonsFontsPath;
  private Path commonsLibsPath;
  private Path commonsTemplatesPath;
  private Path commonsComponentsTemplatesPath;
  private Path commonsThemesPath;
  private Path commonsMailTemplatesPath;
  private Path sitesSourcesPath;
  private Path commonSourcesPath;
  private Path sitesPath;
  private Path clusterFilesPath;
  private Path clusterFileSelectLogPath;
  private Path clusterFileLogPath;

  private final APIConfiguration apiConfiguration = new APIConfiguration();

  private Configuration() {
    // Just protecting the singleton
  }

  public static String get(String key) {
    return GlobalConfigurationFile.getInstance().get(key);
  }

  public static Configuration getInstance() {
    return INSTANCE;
  }

  public static Charset getDefaultCharset() {
    return StandardCharsets.UTF_8;
  }

  public static void load() throws ConfigurationException {
    INSTANCE.loadConfiguration();
  }

  private void loadConfiguration() throws ConfigurationException {
    System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tH:%1$tM:%1$tS.%1$tL [%4$s] %2$s : %5$s%n");

    ConfigurationFile configurationFile = new ConfigurationFile();

    Logger.debug("Configuration file FOUND in " + configurationFile.getPath() + ".");
    if (Files.exists(configurationFile.getPath())) {
      try (InputStream is = new FileInputStream(configurationFile.getPath().toFile())) {
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
        sofiaBasePath = getProperty(properties, SYSTEM_HOME_PROPERTY_NAME);
      } catch (FileNotFoundException e) {
        throw new ConfigurationException("Can't find the configuration file");
      } catch (IOException e) {
        throw new ConfigurationException("Can't read the configuration file");
      }
      validateConfiguration();
      createSystemPaths();

      try {
        GlobalConfigurationFile.getInstance().load();
      } catch (JSONParseException e) {
        throw new ConfigurationException(e);
      }

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

  private void createSystemPaths() throws ConfigurationException {
    systemPath = Paths.get(sofiaBasePath).resolve("system");
    if (!Files.isDirectory(systemPath)) {
      throw new RuntimeConfigurationException("The file " + systemPath + " IS NOT a directory.");
    }

    systemResourcesPath = createSystemPath(systemPath, "resources");
    systemDataPath = createSystemPath(systemResourcesPath, "data");
    statesDataPath = createSystemPath(systemDataPath, "states");
    citiesDataPath = createSystemPath(systemDataPath, "cities");
    postalCodesDataPath = createSystemPath(systemDataPath, "postalCodes");
    systemImagesPath = createSystemPath(systemResourcesPath, "images");
    systemLibsPath = createSystemPath(systemResourcesPath, "libs");
    commonSourcesPath = createSystemPath(systemPath, "sources");
    sitesDataPath = createSystemPath(commonSourcesPath, "data");
    commonsFontsPath = createSystemPath(sitesDataPath, "fonts");
    commonsLibsPath = createSystemPath(sitesDataPath, "libs");
    commonsTemplatesPath = createSystemPath(sitesDataPath, "templates");
    commonsComponentsTemplatesPath = createSystemPath(commonsTemplatesPath, "components");
    commonsMailTemplatesPath = createSystemPath(commonsTemplatesPath, "mail");
    commonsThemesPath = createSystemPath(sitesDataPath, "themes");
    sitesSourcesPath = createSystemPath(commonSourcesPath, "sites");
    sitesPath = createSystemPath(systemPath, "sites");
    clusterFilesPath = createSystemPath(systemResourcesPath, "cluster");

    clusterFileSelectLogPath = clusterFilesPath.resolve("select.log");
    clusterFileLogPath = clusterFilesPath.resolve("data.log");

  }

  private Path createSystemPath(Path base, String name) throws ConfigurationException {
    Path path = base.resolve(name);
    try {
      Files.createDirectories(path);
    } catch (IOException e) {
      throw new ConfigurationException("Can't create the system path: " + path);
    }
    return path;
  }

  public void validateConfiguration() throws ConfigurationException {
    Utils.consoleOutLn("Validate environment configuration.");
    if (!DEVELOPMENT_ENVIRONMENT.equals(environment) && !PRODUCTION_ENVIRONMENT.equals(environment)) {
      throw new ConfigurationException(
              "Invalid value " + environment + " for environment. MUST BE " + DEVELOPMENT_ENVIRONMENT + " or " + PRODUCTION_ENVIRONMENT);
    }
    Utils.consoleOut("Test connection.");
    testConnection();
    Utils.consoleOutLn(" OK");
  }

  private void testConnection() throws ConfigurationException {
    try {
      Database.getConnection(null, 1);
    } catch (SQLException e) {
      throw new ConfigurationException("Invalid configuration for database. " + e.getMessage(), e);
    }
  }

  public void loadAPIConfiguration(DatabaseCreators dataCreators) throws ConfigurationException {
    Path apiConfigurationFilePath = Configuration.getInstance().getSofiaAPIConfigurationPath();
    Logger.debug("Load Sofia API configuration file: %s", apiConfigurationFilePath);
    apiConfiguration.add(apiConfigurationFilePath);

    for (DataCreator dataCreator : dataCreators) {
      apiConfiguration.add(dataCreator);
    }
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

  public Path getSystemResourcesPath() {
    return systemResourcesPath;
  }

  public Path getSystemDataPath() {
    return systemDataPath;
  }

  public Path getSystemImagesPath() {
    return systemImagesPath;
  }

  public Path getSystemLibsPath() {
    return systemLibsPath;
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

  public APIConfiguration getAPIConfiguration() {
    return apiConfiguration;
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

  public Path getClusterFileLogPath() {
    return clusterFileLogPath;
  }

  public Path getClusterFileSelectLogPath() {
    return clusterFileSelectLogPath;
  }

  public String getLoginURL() {
    return "/login.html";
  }

  public Path getSofiaAPIConfigurationPath() {
    return systemPath.resolve("apiDefinition.json");
  }
}
