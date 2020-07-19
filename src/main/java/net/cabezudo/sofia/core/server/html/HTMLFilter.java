package net.cabezudo.sofia.core.server.html;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.creator.InvalidFragmentTag;
import net.cabezudo.sofia.core.creator.LibraryVersionConflictException;
import net.cabezudo.sofia.core.creator.SiteCreationException;
import net.cabezudo.sofia.core.creator.SiteCreator;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.23
 */
public class HTMLFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // Nothing to do here
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException, FileNotFoundException {

    if (req instanceof HttpServletRequest) {
      HttpServletRequest request = (HttpServletRequest) req;

      String serverName = request.getServerName();
      Site site;
      try {
        User owner = UserManager.getInstance().getAdministrator();
        site = SiteManager.getInstance().getByHostame(serverName, owner);
      } catch (SQLException e) {
        throw new ServletException(e);
      }
      req.setAttribute("site", site);

      String requestURI = request.getRequestURI();
      if ("/".equals(requestURI)) {
        requestURI = "/index.html";
      }
      if (requestURI.endsWith("html")) {

        String lastPage = (String) request.getSession().getAttribute("thisPage");
        String thisPage = requestURI;
        if (request.getQueryString() != null) {
          thisPage += "?" + request.getQueryString();
        }
        request.getSession().setAttribute("thisPage", thisPage);
        if (lastPage == null || !lastPage.equals(requestURI)) {
          request.getSession().setAttribute("lastPage", lastPage);
        }

        if (Environment.getInstance().isDevelopment()) {
          try {
            SiteCreator.getInstance().createPage(site, requestURI);
          } catch (SQLException | IOException | InvalidFragmentTag e) {
            if (Environment.getInstance().isDevelopment()) {
              e.printStackTrace();
            }
            throw new ServletException(e);
          } catch (JSONParseException | SiteCreationException | LibraryVersionConflictException e) {
            throw new ServletException(e);
          }
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
