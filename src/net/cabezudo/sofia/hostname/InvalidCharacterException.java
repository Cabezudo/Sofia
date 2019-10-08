package net.cabezudo.sofia.hostname;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.06.22
 */
public class InvalidCharacterException extends HostnameException {

  private final Character c;

  public InvalidCharacterException(Character c) {
    this.c = c;
  }

  public Character getChar() {
    return c;
  }
}
