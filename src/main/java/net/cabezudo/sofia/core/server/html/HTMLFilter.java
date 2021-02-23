package net.cabezudo.sofia.core.server.html;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.creator.InvalidFragmentTag;
import net.cabezudo.sofia.core.creator.LibraryVersionConflictException;
import net.cabezudo.sofia.core.creator.SiteCreationException;
import net.cabezudo.sofia.core.creator.SiteCreator;
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
    if (req instanceof HttpServletRequest) {
      SofiaHTMLServletRequest request = new SofiaHTMLServletRequest((HttpServletRequest) req);
      Site site = (Site) request.getAttribute("site");

      String requestURI = request.getRequestURI();

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
    if (Environment.getInstance().isDevelopment()) {
      Logger.debug("Create pages for URI %s.", requestURI);
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

}
