package net.cabezudo.sofia.core.logger;

import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.cabezudo.sofia.logger.Level;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.15
 */
public class Logger {

  private static final DateFormat sdf = SimpleDateFormat.getDateTimeInstance();

  private static Level level = Level.INFO;

  public static void log(Level level, String message, Object... parameters) {
    log(level, null, null, message, parameters);
  }

  public static void log(Level level, String clazz, String method, String message, Object... parameters) {
    if (Logger.level.compareTo(level) > 0) {
//      return;
    }
    Date date = new Date();
    String metadata = level + (clazz == null ? "" : ":" + clazz) + (method == null ? "" : ":" + method);

    String fullMessage;
    if (parameters.length == 0) {
      fullMessage = sdf.format(date) + " [" + metadata + "] " + message;
    } else {
      fullMessage = sdf.format(date) + " [" + metadata + "] " + String.format(message, parameters);
    }
    System.out.println(fullMessage);
//    try (FileWriter writer = new FileWriter("output.txt", true)) {
//      writer.write(fullMessage);
//      writer.write('\n');
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
  }

  public static void log(Level level, Throwable cause) {
    if (Logger.level.compareTo(level) > 0) {
      return;
    }
    cause.printStackTrace();
  }

  public static void finest(String message, Object... parameters) {
    log(Level.FINEST, message, parameters);
  }

  public static void fine(String message, Object... parameters) {
    fine(null, null, message, parameters);
  }

  public static void fine(String clazz, String method, String message, Object... parameters) {
    log(Level.FINE, clazz, method, message, parameters);
  }

  public static void fine(PreparedStatement ps) {
    fine(null, null, ps);
  }

  public static void fine(String clazz, String method, PreparedStatement ps) {
    String psString = ps.toString();
    int i = psString.indexOf(": ");
    String message = psString.substring(i + 2);
    fine(clazz, method, message);
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
    log(Level.WARNING, e);
  }

  public static void severe(Throwable e) {
    log(Level.SEVERE, e);
  }

  public static void severe(String message, Object... parameters) {
    log(Level.SEVERE, message, parameters);
  }

  public static void severe(String message) {
    log(Level.SEVERE, message);
  }

  public static void setLevel(Level level) {
    info("Set level to " + level + ".");
    Logger.level = level;
  }

}
