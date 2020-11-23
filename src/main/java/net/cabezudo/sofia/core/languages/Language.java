package net.cabezudo.sofia.core.languages;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.11
 */
public class Language {

  private final int id;
  private final String code;

  public Language(int id, String code) {
    this.id = id;
    this.code = code;
  }

  public int getId() {
    return id;
  }

  public String getCode() {
    return code;
  }
}
