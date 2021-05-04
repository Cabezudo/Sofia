package net.cabezudo.sofia.core.sites.domainname;

import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.sites.SitesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class DomainNamesTable {

  public static final String DATABASE_NAME = Configuration.getInstance().getDatabaseName();
  public static final String NAME = "domains";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + DATABASE_NAME + "." + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`siteId` INT, "
          + "`name` VARCHAR(" + DomainName.NAME_MAX_LENGTH + ") NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "FOREIGN KEY (`siteId`) REFERENCES " + SitesTable.DATABASE_NAME + "." + SitesTable.NAME + "(`id`), "
          + "INDEX `iSiteId` (`siteId`), "
          + "UNIQUE INDEX `iName` (`name`)"
          + ") "
          + "CHARACTER SET = UTF8";

  private DomainNamesTable() {
    // Utility classes should not have public constructors
  }

}
