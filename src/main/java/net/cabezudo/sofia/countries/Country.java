package net.cabezudo.sofia.countries;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.05
 */
public class Country {

  private final Integer id;
  private final String name;
  private final int phoneCode;
  private final String twoLettersCountryCode;

  public Country(int id, String name, int phoneCode, String twoLettersCountryCode) {
    this.id = id;
    this.name = name;
    this.phoneCode = phoneCode;
    this.twoLettersCountryCode = twoLettersCountryCode;
  }

  @Override
  public String toString() {
    return "[id = " + getId() + ", name = " + getName() + ", phoneCode = " + getPhoneCode() + ", twoLetterCountryCode = " + getTwoLetterCountryCode() + "]";
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getPhoneCode() {
    return phoneCode;
  }

  public String getTwoLetterCountryCode() {
    return twoLettersCountryCode;
  }
}
