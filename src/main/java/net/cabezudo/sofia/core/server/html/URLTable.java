package net.cabezudo.sofia.core.server.html;

import net.cabezudo.sofia.core.sites.SitesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.17
 */
public class URLTable {

  private URLTable() {
    // Nothing to do here
  }
  public static final String NAME = "url";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`siteId` INT NOT NULL, "
          + "`serverName` VARCHAR(50) NOT NULL, "
          + "`companyPath` VARCHAR(50) NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "UNIQUE INDEX `iSiteServerName` (`siteId`, `serverName`), "
          + "FOREIGN KEY (`siteId`) REFERENCES " + SitesTable.NAME + "(`id`)"
          + ") "
          + "CHARACTER SET = UTF8";
}
