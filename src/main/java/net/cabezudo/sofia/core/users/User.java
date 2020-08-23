package net.cabezudo.sofia.core.users;

import java.sql.SQLException;
import java.util.Date;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.sites.SiteNotExistException;
import net.cabezudo.sofia.emails.EMail;
import net.cabezudo.sofia.emails.EMailNotExistException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.16
 */
public class User {

  private final int id;
  private final int siteId;
  private Site site;
  private EMail eMail;
  private final Date creationDate;
  private final boolean activated;
  private final String passwordRecoveryUUID;
  private final Date passwordRecoveryDate;

  public User(int id, int siteId, EMail eMail, Date creationDate, boolean activated, String passwordRecoveryUUID, Date passwordRecoveryDate) {
    this.id = id;
    this.siteId = siteId;
    this.eMail = eMail;
    this.creationDate = creationDate;
    this.activated = activated;
    this.passwordRecoveryUUID = passwordRecoveryUUID;
    this.passwordRecoveryDate = passwordRecoveryDate;
  }

  public User(int id, Site site, EMail eMail, Date creationDate, boolean activated, String passwordRecoveryUUID, Date passwordRecoveryDate) {
    this(id, site.getId(), eMail, creationDate, activated, passwordRecoveryUUID, passwordRecoveryDate);
  }

  @Override
  public String toString() {
    return "[ id = " + id + ", email = " + eMail + ", siteId = " + siteId + " ]";
  }

  public int getId() {
    return id;
  }

  public int getSiteId() {
    return siteId;
  }

  public Site getSite(User owner) throws SQLException, SiteNotExistException {
    if (site == null) {
      site = SiteManager.getInstance().getById(siteId, owner);
      if (site == null) {
        throw new SiteNotExistException("Can't find the site with the id " + siteId, id);
      }
    }
    return site;
  }

  public EMail getMail() throws EMailNotExistException, SQLException {
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

  public Site getSite() {
    return site;
  }
}
