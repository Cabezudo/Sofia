package net.cabezudo.sofia.core.users.authorization;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.http.QueryString;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.system.SystemMonitor;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.core.users.autentication.NotLoggedException;
import net.cabezudo.sofia.core.users.permission.PermissionTypeManager;
import net.cabezudo.sofia.core.users.profiles.PermissionType;
import net.cabezudo.sofia.core.webusers.WebUserDataManager;
import net.cabezudo.sofia.core.webusers.WebUserDataManager.ClientData;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.23
 */
public class HTMLAuthorizationFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // Nothing to do here
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

    Logger.all(Logger.getLevel().name());
    Site site = (Site) req.getAttribute("site");
    Logger.all("Site: %s", site);
    if (req instanceof HttpServletRequest) {
      HttpServletRequest request = (HttpServletRequest) req;
      HttpServletResponse response = (HttpServletResponse) res;

      ClientData clientData = (ClientData) request.getSession().getAttribute("clientData");
      User user = null;
      try {
        if (clientData != null) {
          Logger.fine("User FOUND in client data.");
          user = clientData.getUser();
        } else {
          Logger.fine("User NOT FOUND in client data.");
        }
        if (Environment.getInstance().isDevelopment()) {
          QueryString queryString = new QueryString(request);
          List<String> userParameterList = queryString.get("user");
          if (userParameterList != null && !userParameterList.isEmpty()) {
            String email = userParameterList.get(0);
            Logger.fine("User email FOUND in url parameters: " + email);
            user = UserManager.getInstance().getByEMail(email, site);
            if (clientData == null) {
              clientData = WebUserDataManager.getInstance().get(request);
            }
            clientData.setUser(user);
            request.getSession().setAttribute("clientData", clientData);
          }
        }
      } catch (SQLException e) {
        SystemMonitor.log(e);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return;
      }

      request.getSession().setAttribute("user", user);

      String requestURI = request.getRequestURI();
      Logger.fine("Request path: " + requestURI);
      Path path = Paths.get(requestURI);
      if (requestURI.endsWith("/")) {
        path = Paths.get(requestURI + "/index.html");
        Logger.fine("NO FILE FOUND in path, add index.");
      }
      Logger.fine("Path: " + path);
      if (path.toString().endsWith("html")) {
        try {
          Logger.fine("Check for permissions.");
          PermissionType permissionType = PermissionTypeManager.getInstance().get("read", site);
          if (!AuthorizationManager.getInstance().hasAuthorization(path.toString(), user, permissionType, site)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
          }
        } catch (NotLoggedException e) {
          if (requestURI.endsWith("html")) {
            String goBackPage = requestURI;
            if (request.getQueryString() != null) {
              goBackPage += "?" + request.getQueryString();
            }
            request.getSession().setAttribute("goBackPage", goBackPage);
          }
          Logger.debug("Not logged. Redirect to login.");
          response.sendRedirect(Configuration.getInstance().getLoginURL());
          return;
        } catch (SQLException e) {
          SystemMonitor.log(e);
          response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          return;
        }
      }
    }

    chain.doFilter(req, res);
  }

  @Override
  public void destroy() {
    // Nothing to do here
  }
}
