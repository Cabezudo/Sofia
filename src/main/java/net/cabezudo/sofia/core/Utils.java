package net.cabezudo.sofia.core;

import java.util.Arrays;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.16
 */
public class Utils {

  public static void consoleOut(String message) {
    System.out.print(message);
  }

  public static void consoleOutLn(String message) {
    System.out.println(message);
  }

  public static String toHour(int time) {
    int minutesRemaind = time / 60;
    int minutes = minutesRemaind % 60;
    int houresRemaind = minutesRemaind / 60;
    int houres = houresRemaind % 60;
    return (houres > 9 ? houres : "0" + houres) + ":" + (minutes > 9 ? minutes : "0" + minutes);
  }

  private Utils() {
    // Nothing to do here. Utility classes should not have public constructors.
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
