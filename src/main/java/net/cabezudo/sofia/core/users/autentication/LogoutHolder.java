package net.cabezudo.sofia.core.users.autentication;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.webusers.WebUserDataManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.13
 */
public class LogoutHolder extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      WebUserDataManager.WebUserData clientData = (WebUserDataManager.WebUserData) request.getSession().getAttribute("clientData");
      if (clientData != null) {
        clientData.setUser(null);
      }
      request.removeAttribute("user");
      response.sendRedirect("/index.html");
    } catch (SQLException e) {
      // TODO responder algo mejor 
      throw new ServletException(e);
    }
  }
}
