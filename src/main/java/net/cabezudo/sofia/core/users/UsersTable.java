package net.cabezudo.sofia.core.users;

import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.sites.SitesTable;
import net.cabezudo.sofia.emails.EMailsTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.17
 */
public class UsersTable {

  public static final String DATABASE_NAME = Configuration.getInstance().getDatabaseName();
  public static final String NAME = "users";
  public static final String CREATION_QUERY
          = "CREATE TABLE " + NAME + " "
          + "("
          + "`id` INT NOT NULL AUTO_INCREMENT, "
          + "`site` INT NOT NULL, "
          + "`eMail` INT NOT NULL, "
          + "`password` BINARY(64) NOT NULL, "
          + "`creationDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP , "
          + "`activated` BOOLEAN NOT NULL DEFAULT FALSE, "
          + "`passwordRecoveryUUID` VARCHAR(72) DEFAULT NULL, "
          + "`passwordRecoveryDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
          + "PRIMARY KEY (`id`), "
          + "INDEX `iSiteEMail` (`site`, `eMail`), "
          + "UNIQUE INDEX `iSiteEMailPassword` (`site`, `eMail`, `password`), "
          + "UNIQUE INDEX `iPasswordRecoveryUUID` (`passwordRecoveryUUID`), "
          + "FOREIGN KEY (`site`) REFERENCES " + SitesTable.DATABASE_NAME + "." + SitesTable.NAME + "(`id`), "
          + "FOREIGN KEY (`eMail`) REFERENCES " + EMailsTable.DATABASE_NAME + "." + EMailsTable.NAME + "(`id`)"
          + ") "
          + "CHARACTER SET=UTF8";
}
