package net.cabezudo.sofia.core.users;

import java.util.Date;
import net.cabezudo.sofia.emails.EMail;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.02.25
 */
class UserForList {

  private final int id;
  private final int siteId;
  private final String siteName;
  private final EMail eMail;
  private final Date creationDate;
  private final boolean activated;

  UserForList(int id, int siteId, String siteName, EMail eMail, Date creationDate, boolean activated) {
    this.id = id;
    this.siteId = siteId;
    this.siteName = siteName;
    this.eMail = eMail;
    this.creationDate = creationDate;
    this.activated = activated;
  }

  public int getId() {
    return id;
  }

  public int getSiteId() {
    return siteId;
  }

  public String getSiteName() {
    return siteName;
  }

  public EMail getEMail() {
    return eMail;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public boolean isActivated() {
    return activated;
  }

}
