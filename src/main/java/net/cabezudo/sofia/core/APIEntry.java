package net.cabezudo.sofia.core;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.10.19
 */
public class APIEntry {

  private final String path;
  private final String className;

  APIEntry(String path, String className) {
    this.path = path;
    this.className = className;
  }

  public String getPath() {
    return path;
  }

  public String getClassName() {
    return className;
  }
}
