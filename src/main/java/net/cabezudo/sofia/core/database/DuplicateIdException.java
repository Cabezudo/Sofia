package net.cabezudo.sofia.core.database;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.03.29
 */
public class DuplicateIdException extends RuntimeException {

  public DuplicateIdException(String message) {
    super(message);
  }
}
