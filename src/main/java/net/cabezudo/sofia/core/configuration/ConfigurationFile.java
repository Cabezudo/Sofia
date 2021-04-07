package net.cabezudo.sofia.core.configuration;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.05
 */
class ConfigurationFile {

  private static final String CONFIGURATION_FILENAME = "sofia.configuration.properties";

  private final Path path;

  ConfigurationFile() throws ConfigurationException {
    Path intendedPath = getConfigurationFilePath();
    checkAndCreateConfigurationFile(intendedPath);
    path = intendedPath;
  }

  private Path getConfigurationFilePath() {
    String userHomePath;
    userHomePath = searchOn(System.getProperty("user.dir") + System.getProperty("file.separator") + CONFIGURATION_FILENAME);
    if (userHomePath != null) {
      return Paths.get(userHomePath);
    }
    userHomePath = searchOn(System.getProperty("user.home") + System.getProperty("file.separator") + CONFIGURATION_FILENAME);
    if (userHomePath != null) {
      return Paths.get(userHomePath);
    }
    return null;
  }

  private String searchOn(String path) {
    Logger.info("Check for configuration file in " + path + ".");
    if (Files.exists(Paths.get(path))) {
      return path;
    }
    return null;
  }

  private void checkAndCreateConfigurationFile(Path filePath) throws ConfigurationException {
    Utils.consoleOut("Checking configuration file path... ");
    if (filePath == null) {
      Utils.consoleOutLn("Not found.");
      if (System.console() != null) {
        Utils.consoleOut("Create configuration file example? [Y/n]: ");
        String createConfigurationFile = System.console().readLine();
        if (createConfigurationFile.isBlank() || "y".equalsIgnoreCase(createConfigurationFile)) {
          try {
            Path configurationFilePath = createConfigurationFile();
            Utils.consoleOutLn("Configure the file " + configurationFilePath + " and run the server again.");
            System.exit(0);
          } catch (ConfigurationException e) {
            Utils.consoleOutLn(e.getMessage());
            System.exit(1);
          }
        }
      }
      Logger.severe("Configuration file not found.");
      System.exit(1);
    }
    Utils.consoleOutLn("OK");
  }

  private Path createConfigurationFile() throws ConfigurationException {
    String directoryName = System.getProperty("user.home");
    Path basePath = Paths.get(directoryName);
    Path filePath = basePath.resolve(CONFIGURATION_FILENAME);
    if (Files.exists(filePath)) {
      throw new RuntimeConfigurationException("The file " + filePath + " already exists.");
    }
    if (Files.isWritable(basePath)) {
      try {
        Files.createFile(path);
      } catch (IOException e) {
        throw new ConfigurationException("Can't create the file. " + e.getMessage());
      }
      try {
        try (FileWriter out = new FileWriter(filePath.toFile())) {
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

  Path getPath() {
    return path;
  }
}
