package net.cabezudo.sofia.core;

import java.util.List;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.23
 */
public class StartOptions {

  private String invalidArgument;
  private boolean help;
  private boolean debug;
  private boolean configureRoot;
  private boolean dropDatabase;
  private boolean ide;

  public StartOptions(List<String> arguments) {
    for (String argument : arguments) {
      switch (argument) {
        case "--help":
        case "-h":
          help = true;
          break;
        case "--debug":
        case "-d":
          Logger.info("Debug activated.");
          debug = true;
          break;
        case "--configureRoot":
        case "-cr":
          Logger.info("Root configuration activated");
          configureRoot = true;
          break;
        case "--dropDatabase":
        case "-dd":
          Logger.info("Command line drop database activated.");
          dropDatabase = true;
          break;
        case "--ide":
        case "-i":
          Logger.info("IDE mode on");
          ide = true;
          break;
        default:
          invalidArgument = argument;
          break;
      }
    }
  }

  public String getHelp() {
    StringBuilder sb = new StringBuilder();
    sb.append("-h, --help - This help.").append('\n');
    sb.append("-d, --debug - Print all the debug information.").append('\n');
    sb.append("-cr, --configureRoot - Configure the root information.").append('\n');
    sb.append("-dd, --dropDatabase - Drop de database and create a new one.").append('\n');
    sb.append("-i, --ide - Configure the system to work inside an IDE").append('\n');
    return sb.toString();
  }

  public boolean hasHelp() {
    return help;
  }

  public boolean hasDebug() {
    return debug;
  }

  public boolean hasConfigureRoot() {
    return configureRoot;
  }

  public boolean hasDropDatabase() {
    return dropDatabase;
  }

  public String getInvalidArgument() {
    return invalidArgument;
  }

  public boolean hasInvalidArgument() {
    return invalidArgument != null;
  }

  public boolean hasIDE() {
    return ide;
  }
}
