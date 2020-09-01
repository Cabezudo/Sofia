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
      SofiaHTMLServletRequest request = new SofiaHTMLServletRequest((HttpServletRequest) req);
      changeURL(request);
      chain.doFilter(request, res);
    } else {
      chain.doFilter(req, res);
    }
  }

  private void changeURL(SofiaHTMLServletRequest request) {
    DomainName domainName = new DomainName(request.getServerName());
    String requestURI = request.getRequestURI();

    if (domainName.match("local.**")) {
      request.setServerName(domainName.parent().toString());
      Logger.debug("local.** change : serverName is now %s", domainName);
    }

    if (domainName.match("api.**")) {
      request.setServerName(domainName.parent().toString());
      request.setRequestURI("/api" + requestURI);
    }

    if (domainName.match("admin.**")) {
      request.setServerName(domainName.parent().toString());
      request.setRequestURI("/admin" + requestURI);
    }
  }

  @Override
  public void destroy() {
    // Nothing to do here
  }
}
