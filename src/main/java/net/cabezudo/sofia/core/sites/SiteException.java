package net.cabezudo.sofia.core.sites;

import java.sql.SQLException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.02.18
 */
public class SiteException extends Exception {

  public SiteException(SQLException cause) {
    super(cause);
  }
}
