package net.cabezudo.sofia.countries;

import net.cabezudo.sofia.core.words.Word;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.05
 */
public class Country {

  private final Integer id;
  private final String twoLettersCountryCode;
  private final Word name;
  private final int phoneCode;

  public Country(int id, String twoLettersCountryCode, Word name, int phoneCode) {
    this.id = id;
    this.twoLettersCountryCode = twoLettersCountryCode;
    this.name = name;
    this.phoneCode = phoneCode;
  }

  @Override
  public String toString() {
    return "[id = " + getId() + ", name = " + getName() + ", twoLetterCountryCode = " + getTwoLetterCountryCode() + ", phoneCode = " + getPhoneCode() + "]";
  }

  public Integer getId() {
    return id;
  }

  public String getTwoLetterCountryCode() {
    return twoLettersCountryCode;
  }

  public Word getName() {
    return name;
  }

  public int getPhoneCode() {
    return phoneCode;
  }
}
