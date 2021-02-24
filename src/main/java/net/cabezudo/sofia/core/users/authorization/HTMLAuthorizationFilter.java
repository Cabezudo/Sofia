package net.cabezudo.sofia.core.users.authorization;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.http.QueryString;
import net.cabezudo.sofia.core.http.SessionManager;
import net.cabezudo.sofia.core.http.WebUserData;
import net.cabezudo.sofia.core.server.html.SofiaHTMLServletRequest;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.system.SystemMonitor;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.core.users.autentication.NotLoggedException;
import net.cabezudo.sofia.core.users.permission.PermissionTypeManager;
import net.cabezudo.sofia.core.users.profiles.PermissionType;
import net.cabezudo.sofia.core.webusers.WebUserDataManager;
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

    Site site = (Site) req.getAttribute("site");
    Logger.all("Site: %s", site);
    if (req instanceof SofiaHTMLServletRequest) {
      SofiaHTMLServletRequest request = (SofiaHTMLServletRequest) req;
      HttpServletResponse response = (HttpServletResponse) res;

      User user = null;
      try {
        SessionManager sessionManager = new SessionManager(request);
        WebUserData webUserData = sessionManager.getWebUserData();
        if (webUserData != null) {
          Logger.fine("Web user data FOUND on session.");
          user = webUserData.getUser();
        } else {
          Logger.fine("Web user data NOT FOUND on session.");
        }
        if (Environment.getInstance().isDevelopment()) {
          QueryString queryString = new QueryString(request);
          List<String> userParameterList = queryString.get("user");
          if (userParameterList != null && !userParameterList.isEmpty()) {
            String email = userParameterList.get(0);
            Logger.fine("User email FOUND in url parameters: " + email);
            user = UserManager.getInstance().getByEMail(email, site);
            if (webUserData == null) {
              webUserData = WebUserDataManager.getInstance().get(request);
            }
            webUserData.setUser(user);
            sessionManager.setSessionWebUserData();
            WebUserDataManager.getInstance().update(webUserData);
          }
        }
      } catch (ClusterException e) {
        SystemMonitor.log(e);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return;
      }

      String requestURI = request.getRequestURI();
      Logger.fine("Server name: " + request.getServerName());
      Logger.fine("Request uri: " + requestURI);
      Path path = Paths.get(requestURI);
      if (path.toString().endsWith("html")) {
        try {
          Logger.fine("Check for read permissions for user " + user + ".");
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
        } catch (ClusterException e) {
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
