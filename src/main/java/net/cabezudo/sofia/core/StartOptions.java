package net.cabezudo.sofia.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;
import net.cabezudo.sofia.logger.Level;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.23
 */
public class StartOptions {

  private boolean help;
  private boolean debug;
  private Path customConfigurationFilePath;
  private boolean configureAdministrator;
  private boolean changeUserPassword;
  private boolean dropDatabase;
  private boolean createTestData;
  private boolean ide;

  public StartOptions(Queue<String> arguments) throws InvalidParameterException {
    Logger.debug("Check start options.");
    if (arguments != null && arguments.isEmpty()) {
      Logger.debug("%s command lines argument FOUND.", arguments.size());
    }
    while (arguments != null && !arguments.isEmpty()) {
      String argument = arguments.poll();
      String[] parts = argument.split("=");
      String parameter = parts[0];
      switch (parameter) {
        case "--help":
        case "-h":
          help = true;
          break;
        case "--debug":
        case "-d":
          Logger.setLevel(Level.DEBUG);
          Logger.debug("Debug activated.");
          debug = true;
          break;
        case "--configurationFile":
        case "-cf":
          String customConfigurationFile;
          if (parts.length == 1) {
            throw new InvalidParameterException("No value for configuration file parameter.");
          } else {
            customConfigurationFile = parts[1];
          }
          if (customConfigurationFile != null) {
            customConfigurationFilePath = Paths.get(customConfigurationFile);
            if (!Files.exists(customConfigurationFilePath)) {
              throw new InvalidParameterException("Configuration file not exist: " + customConfigurationFilePath);
            }
          }

          Logger.info("Custom configuration file %s", customConfigurationFilePath);
          break;
        case "--createAdministrator":
        case "-ca":
          Logger.debug("Administrator configuration activated");
          configureAdministrator = true;
          break;
        case "--changePassword":
        case "-cp":
          Logger.debug("Change user password");
          changeUserPassword = true;
          break;
        case "--dropDatabase":
        case "-dd":
          Logger.debug("Command line drop database activated.");
          dropDatabase = true;
          break;
        case "--createTestData":
        case "-ctd":
          Logger.debug("Command line create default test data.");
          createTestData = true;
          break;

        case "--ide":
        case "-i":
          Logger.setLevel(Level.ALL);
          Logger.all("IDE mode on");
          ide = true;
          break;
        default:
          throw new InvalidParameterException("Invalid parameter: " + parameter);
      }
    }
  }

  public static String getHelp() {
    StringBuilder sb = new StringBuilder();
    sb.append("-h, --help - This help.").append('\n');
    sb.append("-d, --debug - Print all the debug information.").append('\n');
    sb.append("-cr, --createAdministrator - Configure a system administrator.").append('\n');
    sb.append("-cf, --configurationFile=FILE_NAME - Read the configuration from a specific file.").append('\n');
    sb.append("-cp, --changePassword - Change a user password.").append('\n');
    sb.append("-dd, --dropDatabase - Drop de database and create a new one.").append('\n');
    sb.append("-ctd, --createTestData - Create default test data.").append('\n');
    sb.append("-i, --ide - Configure the system to work inside an IDE").append('\n');
    return sb.toString();
  }

  public boolean hasHelp() {
    return help;
  }

  public boolean hasDebug() {
    return debug;
  }

  public Path getCustomConfigurationFilePath() {
    return customConfigurationFilePath;
  }

  public boolean hasConfigureAdministrator() {
    return configureAdministrator;
  }

  public boolean hasChangeUserPassword() {
    return changeUserPassword;
  }

  public boolean hasDropDatabase() {
    return dropDatabase;
  }

  public boolean hasCreateTestData() {
    return createTestData;
  }

  public boolean hasIDE() {
    return ide;
  }
}
