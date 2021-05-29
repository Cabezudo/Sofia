package net.cabezudo.sofia.core.languages;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.http.SessionManager;
import net.cabezudo.sofia.core.webusers.WebUserData;
import net.cabezudo.sofia.core.webusers.WebUserDataManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.11.25
 */
public class ChangeLanguageServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String referrer = request.getHeader("referer");
    String twoLetterCodeLanguage = request.getQueryString();
    SessionManager sessionManager = new SessionManager(request);
    try {
      WebUserData webUserData = sessionManager.getWebUserData();
      if (webUserData == null) {
        response.sendRedirect(referrer);
        return;
      }
      webUserData.setActualLanguage(twoLetterCodeLanguage);
      sessionManager.setSessionWebUserData();
      WebUserDataManager.getInstance().update(webUserData);
      response.getWriter().print("{ \"language\": " + webUserData.getActualLanguage().toJSONTree() + " }");
    } catch (ClusterException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can't set the language");
    } catch (InvalidTwoLettersCodeException e) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
}
