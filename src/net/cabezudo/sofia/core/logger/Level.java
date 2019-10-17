package net.cabezudo.sofia.core.logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.24
 */
public class Level implements Comparable<Level> {

  public static final Level FINEST = new Level(0);
  public static final Level FINE = new Level(1);
  public static final Level DEBUG = new Level(2);
  public static final Level INFO = new Level(3);
  public static final Level WARNING = new Level(4);
  public static final Level SEVERE = new Level(5);
  private final int level;

  private Level(int level) {
    this.level = level;
  }

  @Override
  public int compareTo(Level level) {
    return Integer.compare(this.level, level.level);
  }
}
