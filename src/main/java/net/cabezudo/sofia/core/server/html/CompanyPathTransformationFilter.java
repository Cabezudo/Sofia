package net.cabezudo.sofia.core.server.html;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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

    if (req instanceof SofiaServletRequest) {
      SofiaServletRequest request = (SofiaServletRequest) req;
      String requestURI = request.getRequestURI();

      if (requestURI.startsWith("/donbeto")) {
        if (!requestURI.endsWith("variables.js")) {
          requestURI = "/company" + requestURI.substring("/donbeto".length());
        }
        Logger.debug("company directory found: requestURI is now %s", requestURI);
        request.setRequestURI(requestURI);
        request.setAttribute("urlChanged", Boolean.TRUE);
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
