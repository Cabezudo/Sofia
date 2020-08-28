package net.cabezudo.sofia.core.server.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import net.cabezudo.sofia.core.http.domains.DomainName;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.25
 */
public class SofiaHTMLServletRequest extends HttpServletRequestWrapper {

  private DomainName domainName;
  private String requestURI;
  private String pathInfo;

  public SofiaHTMLServletRequest(HttpServletRequest request) {
    super(request);
    domainName = new DomainName(request.getServerName());
    requestURI = request.getRequestURI();
    pathInfo = requestURI;
  }

  @Override
  public String getServerName() {
    return domainName.toString();
  }

  public void setRequestURI(String requestURI) {
    this.requestURI = requestURI;
    this.pathInfo = requestURI;
  }

  @Override
  public String getRequestURI() {
    return requestURI;
  }

  @Override
  public String getPathInfo() {
    return pathInfo;
  }

  public void setServerName(DomainName domainName) {
    this.domainName = domainName;
  }
}
