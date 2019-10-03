package net.cabezudo.sofia.core.users.authorization;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.system.SystemMonitor;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.autentication.NotLoggedException;
import net.cabezudo.sofia.core.users.permission.PermissionTypeManager;
import net.cabezudo.sofia.core.users.profiles.PermissionType;
import net.cabezudo.sofia.core.webusers.WebUserDataManager.ClientData;

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

    if (req instanceof HttpServletRequest) {
      HttpServletRequest request = (HttpServletRequest) req;
      HttpServletResponse response = (HttpServletResponse) res;

      ClientData clientData = (ClientData) request.getSession().getAttribute("clientData");
      User user = null;
      if (clientData != null) {
        user = clientData.getUser();
      }
      request.setAttribute("user", user);

      String requestURI = request.getRequestURI();
      Path path = Paths.get(requestURI);
      if ("/".equals(path.toString())) {
        path = Paths.get("/index.html");
      }
      if (path.toString().endsWith("html")) {
        try {
          PermissionType permissionType = PermissionTypeManager.getInstance().get("read", site);
          if (!AuthorizationManager.getInstance().hasAuthorization(path.toString(), user, permissionType, site)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
          }
        } catch (NotLoggedException e) {
          if (requestURI.endsWith("html")) {
            String comebackPage = requestURI;
            if (request.getQueryString() != null) {
              comebackPage += "?" + request.getQueryString();
            }
            System.out.println(comebackPage);
            request.getSession().setAttribute("comebackPage", comebackPage);
          }
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
