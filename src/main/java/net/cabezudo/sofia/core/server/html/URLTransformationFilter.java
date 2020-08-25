package net.cabezudo.sofia.core.server.html;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
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
      HttpServletRequest request = new SofiaServletRequest((HttpServletRequest) req);
      chain.doFilter(request, res);
    } else {
      chain.doFilter(req, res);
    }
  }

  @Override
  public void destroy() {
    // Nothing to do here
  }

  private class SofiaServletRequest extends HttpServletRequestWrapper {

    private DomainName domainName;
    private String requestURI;

    private SofiaServletRequest(HttpServletRequest request) {
      super(request);

      domainName = new DomainName(request.getServerName());
      requestURI = request.getRequestURI();

      changeURL(request);

    }

    @Override
    public String getServerName() {
      return domainName.toString();
    }

    @Override
    public String getRequestURI() {
      return requestURI;
    }

    private void changeURL(HttpServletRequest request) {
      if (domainName.match("local.**")) {
        domainName = domainName.parent();
        Logger.debug("local.** change : serverName is now %s.", domainName);
        // The rule is set to continue to the next rule
      }

      if (domainName.match("api.**")) {
        domainName = domainName.parent();
        requestURI = "/api" + requestURI;
        Logger.debug("api.** change : requestURI is now %s for domain name %s.", requestURI, domainName);
        return; // The rule is set to not continue to the next rule
      }

      if (domainName.match("admin.**")) {
        domainName = domainName.parent();
        requestURI = "/admin" + requestURI;
        Logger.debug("admin.** change : requestURI is now %s for domain name %s.", requestURI, domainName);
      }

      // TODO change this in order to use the database for add new domains to this behavior
      if (domainName.match("{company}.cdmx.menu")) {
        DomainName newDomainName = domainName.parent();
        String company = domainName.getValue("company");
        requestURI = "/" + company + requestURI;
        request.setAttribute("company", company);
        Logger.debug("company name found : requestURI is now %s for domain name %s.", requestURI, newDomainName);
      }
    }
  }
}
