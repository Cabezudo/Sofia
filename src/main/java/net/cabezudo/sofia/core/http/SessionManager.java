package net.cabezudo.sofia.core.http;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.webusers.WebUserDataManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.02.23
 */
public class SessionManager {

  private final WebUserData webUserData;
  private final HttpServletRequest request;

  public SessionManager(HttpServletRequest request) throws ServletException {
    this.request = request;
    WebUserData webUserDataFromSession = (WebUserData) request.getSession().getAttribute("webUserData");
    if (webUserDataFromSession == null) {
      try {
        WebUserData webUserDataFromDatabase = WebUserDataManager.getInstance().get(request);
        if (webUserDataFromDatabase == null) {
          webUserData = WebUserDataManager.getInstance().add(request);
        } else {
          webUserData = webUserDataFromDatabase;
        }
      } catch (ClusterException e) {
        throw new ServletException(e);
      }
      setSessionWebUserData();
    } else {
      webUserData = webUserDataFromSession;
    }
  }

  public WebUserData getWebUserData() throws ClusterException {
    return webUserData;
  }

  public final void setSessionWebUserData() {
    request.getSession().setAttribute("webUserData", webUserData);
  }

  public void setSite(Site site) {
    request.getSession().setAttribute("site", site);
  }

  public Site getSite() {
    return (Site) request.getSession().getAttribute("site");
  }
}
