package net.cabezudo.sofia.core.words;

import java.util.Objects;
import net.cabezudo.sofia.core.languages.Language;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.11.23
 */
public class Word implements Comparable<Word> {

  private final int id;
  private final Language language;
  private final String value;

  public Word(int id, Language language, String word) {
    this.id = id;
    this.language = language;
    this.value = word;
  }

  public Word(Word word) {
    this.id = word.getId();
    this.language = word.getLanguage();
    this.value = word.getValue();
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

  @Override
  public int compareTo(Word word) {
    return value.compareTo(word.value);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Word) {
      Word word = (Word) o;
      return value.equals(word.value);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 83 * hash + Objects.hashCode(this.value);
    return hash;
  }

}
