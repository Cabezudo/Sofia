package net.cabezudo.sofia.core.users.autentication;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.http.SessionManager;
import net.cabezudo.sofia.core.webusers.WebUserData;
import net.cabezudo.sofia.core.webusers.WebUserDataManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.13
 */
public class LogoutHolder extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    SessionManager sessionManager = new SessionManager(request);
    WebUserData webUserData;
    try {
      webUserData = sessionManager.getWebUserData();
      webUserData.setUser(null);
      WebUserDataManager.getInstance().update(webUserData);
    } catch (ClusterException e) {
      throw new ServletException(e);
    }
    response.sendRedirect("/index.html");
  }
}
