package net.cabezudo.sofia.clients;

import net.cabezudo.sofia.core.database.Table;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.13
 */
public class ClientTable extends Table {

  public static final String NAME = "clients";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL, "
          + "PRIMARY KEY (`id`)"
          + ") "
          + "CHARACTER SET=UTF8";
}
