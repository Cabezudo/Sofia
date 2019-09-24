package net.cabezudo.sofia.hosts;

import net.cabezudo.sofia.core.database.Table;
import net.cabezudo.sofia.core.sites.SitesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class HostTable extends Table {

  public static final String NAME = "hosts";
  public static final String CREATION_QUERY
      = "CREATE TABLE " + NAME + " "
      + "("
      + "`id` INT NOT NULL AUTO_INCREMENT, "
      + "`siteId` INT, "
      + "`name` VARCHAR(" + Host.NAME_MAX_LENGTH + ") NOT NULL, "
      + "PRIMARY KEY (`id`), "
      + "FOREIGN KEY (`siteId`) REFERENCES " + SitesTable.NAME + "(`id`), "
      + "INDEX `iSiteId` (`siteId`), "
      + "UNIQUE INDEX `iName` (`name`)"
      + ") "
      + "CHARACTER SET = UTF8";

}
