package net.cabezudo.sofia.core.server.html;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import net.cabezudo.sofia.core.http.domains.DomainName;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.23
 */
public class URLTransformationFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // Nothing to do here
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
    if (req instanceof HttpServletRequest) {
      SofiaServletRequest request = new SofiaServletRequest((HttpServletRequest) req);
      Boolean urlChanged = changeURL(request);
      request.setAttribute("urlChanged", urlChanged);
      chain.doFilter(request, res);
    } else {
      chain.doFilter(req, res);
    }
  }

  boolean changeURL(SofiaServletRequest request) {
    DomainName domainName = new DomainName(request.getServerName());
    String requestURI = request.getRequestURI();

    if (requestURI.endsWith("/")) {
      requestURI += "index.html";
      Logger.fine("NO FILE FOUND in path, add index.");
    }

    Logger.debug("changeURL method for %s", requestURI);
    if (domainName.match("local.**")) {
      domainName = domainName.parent();
      Logger.debug("local.** change : serverName is now %s", domainName);
      // The rule is set to continue to the next rule
    }

    if (domainName.match("api.**")) {
      domainName = domainName.parent();
      requestURI = "/api" + requestURI;
      request.setRequestURI(requestURI);
      Logger.debug("api.** change : requestURI is now %s for domain name %s", requestURI, domainName);
    }

    if (domainName.match("admin.**")) {
      domainName = domainName.parent();
      requestURI = "/admin" + requestURI;
      request.setRequestURI(requestURI);
      Logger.debug("admin.** change : requestURI is now %s for domain name %s", requestURI, domainName);
    }
    // TODO change this in order to use the database for add new domains to this behavior
    if (domainName.match("donbeto.cdmx.menu")) {
      requestURI = "/donbeto" + requestURI;
      request.setRequestURI(requestURI);
      domainName = domainName.parent();
      request.setServerName(domainName);
      Logger.debug("company name found : requestURI is now %s for domain name %s", requestURI, domainName);
      return true;
    }
    return false;
  }

  @Override
  public void destroy() {
    // Nothing to do here
  }
}
