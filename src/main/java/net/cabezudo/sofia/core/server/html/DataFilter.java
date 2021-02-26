package net.cabezudo.sofia.core.server.html;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.http.SessionManager;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.23
 */
public class DataFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // Nothing to do here
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
    if (req instanceof HttpServletRequest) {
      SofiaHTMLServletRequest request = new SofiaHTMLServletRequest((HttpServletRequest) req);

      try {
        String serverName = request.getServerName();
        Site site;
        site = SiteManager.getInstance().getByHostame(serverName);
        if (site == null) {
          throw new ServletException("Can't find the site using " + serverName);
        }
        new SessionManager(request).setSite(site);

      } catch (ClusterException e) {
        if (Environment.getInstance().isDevelopment()) {
          e.printStackTrace();
        }
        throw new ServletException(e);
      }
      chain.doFilter(request, res);
    } else {
      chain.doFilter(req, res);
    }
  }

  @Override
  public void destroy() {
    // Nothing to do here
  }
}
