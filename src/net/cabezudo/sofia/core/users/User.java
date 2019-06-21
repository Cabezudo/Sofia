package net.cabezudo.sofia.core.users;

import java.sql.SQLException;
import java.util.Date;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.sites.SiteNotExistException;
import net.cabezudo.sofia.emails.EMail;
import net.cabezudo.sofia.emails.EMailManager;
import net.cabezudo.sofia.emails.EMailNotExistException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.16
 */
public class User {

  private final int id;
  private final int siteId;
  private Site site;
  private final int eMailId;
  private EMail eMail;
  private final Date creationDate;
  private final boolean activated;
  private final String passwordRecoveryUUID;
  private final Date passwordRecoveryDate;

  public User(int id, int siteId, int eMailId, Date creationDate, boolean activated, String passwordRecoveryUUID, Date passwordRecoveryDate) {
    this.id = id;
    this.siteId = siteId;
    this.eMailId = eMailId;
    this.creationDate = creationDate;
    this.activated = activated;
    this.passwordRecoveryUUID = passwordRecoveryUUID;
    this.passwordRecoveryDate = passwordRecoveryDate;
  }

  public int getId() {
    return id;
  }

  public int getSiteId() {
    return siteId;
  }

  public Site getSite() throws SQLException, SiteNotExistException {
    if (site == null) {
      site = SiteManager.getInstance().getById(siteId);
      if (site == null) {
        throw new SiteNotExistException("Can't find the site with the id " + siteId, id);
      }
    }
    return site;
  }

  public int getMailId() {
    return eMailId;
  }

  public EMail getMail() throws EMailNotExistException, SQLException {
    if (eMail == null) {
      eMail = EMailManager.getInstance().get(siteId);
      if (eMail == null) {
        throw new EMailNotExistException("Can't find the site with the id " + siteId, id);
      }
    }
    return eMail;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public boolean isActivated() {
    return activated;
  }

  public String getPasswordRecoveryHash() {
    return passwordRecoveryUUID;
  }

  public Date getPasswordRecoveryDate() {
    return passwordRecoveryDate;
  }
}
