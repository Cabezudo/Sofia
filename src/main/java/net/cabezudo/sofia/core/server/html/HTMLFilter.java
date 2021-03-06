package net.cabezudo.sofia.core.server.html;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.company.CompanySubdomainManager;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.creator.InvalidFragmentTag;
import net.cabezudo.sofia.core.creator.LibraryVersionConflictException;
import net.cabezudo.sofia.core.creator.SiteCreationException;
import net.cabezudo.sofia.core.creator.SiteCreator;
import net.cabezudo.sofia.core.http.SessionManager;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.logger.Logger;

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
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
    Logger.debug("HTML filter");
    if (req instanceof HttpServletRequest) {
      SofiaHTMLServletRequest request = new SofiaHTMLServletRequest((HttpServletRequest) req);
      Site site = new SessionManager(request).getSite();

      String requestURI = request.getRequestURI();

      // TODO use an alias manager
      request.getSession().removeAttribute("companyId");

      int i = requestURI.indexOf("/", 1);
      if (i > 0) {
        String subdomain = requestURI.substring(1, i);
        Integer companyId = CompanySubdomainManager.getInstance().get(subdomain);
        if (companyId != null) {
          requestURI = requestURI.replace(subdomain, "restaurant");
          request.setRequestURI(requestURI);
          request.getSession().setAttribute("companyId", companyId);
        }
      }

      if (requestURI.endsWith("/")) {
        requestURI = requestURI + "index.html";
        request.setRequestURI(requestURI);
        Logger.debug("index.html file NO FOUND in path. Add file. Request URI: %s", request.getRequestURI());
      }

      if (requestURI.endsWith(".html")) {
        String lastPage = (String) request.getSession().getAttribute("thisPage");
        String thisPage = requestURI;
        if (request.getQueryString() != null) {
          thisPage += "?" + request.getQueryString();
        }
        request.getSession().setAttribute("thisPage", thisPage);
        if (lastPage == null || !lastPage.equals(requestURI)) {
          request.getSession().setAttribute("lastPage", lastPage);
        }
        createPages(site, requestURI);
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

  private void createPages(Site site, String requestURI) throws ServletException {
    try {
      SiteCreator.getInstance().createPages(site, requestURI);
    } catch (ClusterException | IOException | InvalidFragmentTag e) {
      if (Environment.getInstance().isDevelopment()) {
        e.printStackTrace();
      }
      throw new ServletException(e);
    } catch (JSONParseException | SiteCreationException | LibraryVersionConflictException e) {
      throw new ServletException(e);
    }
  }
}
