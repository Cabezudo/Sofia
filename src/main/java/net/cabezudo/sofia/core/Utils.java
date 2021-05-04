package net.cabezudo.sofia.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.16
 */
public class Utils {

  public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm a z";

  public static void consoleOut(String message) {
    System.out.print(message);
  }

  public static void consoleOutLn(String message) {
    System.out.println(message);
  }

  public static String getDateToString(Date date) {
    DateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    return df.format(date);
  }

  public static String getMillisecondsToTime(long l) {
    int mi = (int) l % 1000;
    int rs = (int) l / 1000;
    int s = rs % 60;
    int rm = rs / 60;
    int m = rm % 60;
    int rh = rm / 60;
    int h = rh / 60;
    return String.format("%02d:%02d:%02d.%03d", h, m, s, mi);
  }

  private Utils() {
    // Utility classes should not have public constructors.
  }

  public static String repeat(char c, int length) {
    char[] array = new char[length];
    Arrays.fill(array, c);
    return new String(array);
  }

  public static String chop(String string, int i) {
    return string.substring(0, string.length() - i);
  }

  public static StringBuilder chop(StringBuilder sb, int i) {
    String s = sb.substring(0, sb.length() - i);
    StringBuilder newStringBuilder = new StringBuilder(s);
    return newStringBuilder;
  }
}
