package net.cabezudo.sofia.core.database.sql;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.05.28
 */
public class InvalidDatabaseFieldException extends RuntimeException {

  public InvalidDatabaseFieldException(String message) {
    super(message);
  }
}
