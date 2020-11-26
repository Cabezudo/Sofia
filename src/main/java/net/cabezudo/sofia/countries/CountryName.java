package net.cabezudo.sofia.countries;

import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.words.Word;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.11.23
 */
public class CountryName extends Word {

  public CountryName(int id, Language language, String word) {
    super(id, language, word);
  }

  CountryName(Word countryName) {
    super(countryName);
  }

}
