package net.cabezudo.sofia.core;

import java.util.Arrays;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.16
 */
public class Utils {

  public static String repeat(char c, int length) {
    char[] array = new char[length];
    Arrays.fill(array, c);
    return new String(array);
  }
}
