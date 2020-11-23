package net.cabezudo.sofia.core.words;

import net.cabezudo.sofia.core.languages.Language;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.11.23
 */
public class Word {

  private final int id;
  private final Language language;
  private final String value;

  public Word(int id, Language language, String word) {
    this.id = id;
    this.language = language;
    this.value = word;

  }

  public int getId() {
    return id;
  }

  public Language getLanguage() {
    return language;
  }

  public String getValue() {
    return value;
  }
}
