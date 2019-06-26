package net.cabezudo.sofia.core.logger;

import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.15
 */
public class Logger {

  private static final DateFormat sdf = SimpleDateFormat.getDateTimeInstance();

  private static Level level = Level.INFO;

  public static void log(Level level, String message, Object... parameters) {
    if (Logger.level.compareTo(level) > 0) {
      return;
    }
    Date date = new Date();
    if (parameters.length == 0) {
      System.out.println(sdf.format(date) + " [" + level + "] " + message);
    } else {
      System.out.println(sdf.format(date) + " [" + level + "] " + String.format(message, parameters));
    }
  }

  public static void finest(String message, Object... parameters) {
    log(Level.FINEST, message, parameters);
  }

  public static void fine(String message, Object... parameters) {
    log(Level.FINE, message, parameters);
  }

  public static void fine(PreparedStatement ps) {
    String psString = ps.toString();
    int i = psString.indexOf(": ");
    String message = psString.substring(i + 2);
    fine(message);
  }

  public static void debug(String message, Object... parameters) {
    log(Level.DEBUG, message, parameters);
  }

  public static void info(String message, Object... parameters) {
    log(Level.INFO, message, parameters);
  }

  public static void warning(String message, Object... parameters) {
    log(Level.WARNING, message, parameters);
  }

  public static void warning(Throwable e) {
    log(Level.WARNING, e.getMessage());
  }

  public static void severe(Throwable e) {
    log(Level.SEVERE, e.getMessage());
  }

  public static void severe(String message) {
    log(Level.SEVERE, message);
  }

  public static void setLevel(Level level) {
    Logger.level = level;
  }

}
