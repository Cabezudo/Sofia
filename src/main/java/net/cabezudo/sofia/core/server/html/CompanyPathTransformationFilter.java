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
public class CompanyPathTransformationFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // Nothing to do here
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
    if (req instanceof HttpServletRequest) {
      SofiaHTMLServletRequest request = new SofiaHTMLServletRequest((HttpServletRequest) req);
      changeURL(request);
      chain.doFilter(request, res);
    } else {
      chain.doFilter(req, res);
    }
  }

  @Override
  public void destroy() {
    // Nothing to do here
  }

  private void changeURL(SofiaHTMLServletRequest request) {
    DomainName domainName = new DomainName(request.getServerName());
    String requestURI = request.getRequestURI();

    // TODO change this in order to use the database for add new domains to this behavior
    if (domainName.match("donbeto.cdmx.menu")) {
      requestURI = "/donbeto" + requestURI;
      request.setRequestURI(requestURI);
      request.setServerName(domainName.parent());
    }

    if (requestURI.startsWith("/donbeto")) {
      Logger.debug("Request start with /donbeto");
      if (!requestURI.endsWith("variables.js")) {
        requestURI = "/company" + requestURI.substring("/donbeto".length());
        request.setRequestURI(requestURI);
        Logger.debug("The file is not variables.js");
      }
      Logger.debug("Company directory FOUND in path. Add file. Request URI: %s", request.getRequestURI());
    }
  }

}
