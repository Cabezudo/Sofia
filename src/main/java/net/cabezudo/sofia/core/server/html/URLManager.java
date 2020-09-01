package net.cabezudo.sofia.core.server.html;

import net.cabezudo.sofia.core.cache.Cache;
import net.cabezudo.sofia.core.http.domains.DomainName;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.01
 */
class URLManager {

  private static final URLManager INSTANCE = new URLManager();

  private URLManager() {
    // Nothing to do here
  }

  static URLManager getInstance() {
    return INSTANCE;
  }

  void changeCompanyHost(SofiaHTMLServletRequest request) {
    String serverName = request.getServerName();
    String requestURI = request.getRequestURI();

    Cache<String, RequestData> requestDataCache = RequestData.getCache();
    RequestData requestData = requestDataCache.get(serverName);

    if (requestData == null) {
      String subdomain = getHostByServerName(serverName);
      if (subdomain != null) {
        requestURI = "/" + subdomain + requestURI;
        DomainName domainName = new DomainName(serverName);
        serverName = domainName.parent().toString();
        requestData = new RequestData(serverName, domainName.toString());
        requestDataCache.put(domainName.toString(), requestData);
        request.setRequestURI(requestURI);
        request.setServerName(serverName);
      }
    } else {
      serverName = requestData.getServerName();
      requestURI = requestData.getRequestURI();
      request.setRequestURI(requestURI);
      request.setServerName(serverName);
    }
  }

  private String getHostByServerName(String serverName) {
    // TODO put this in a database
    if ("donbeto.cdmx.menu".equals(serverName)) {
      return "donbeto";
    }
    return null;
  }

  void changeCompanyPath(SofiaHTMLServletRequest request) {
    String requestURI = request.getRequestURI();

    if (requestURI.startsWith("/donbeto")) {
      Logger.debug("Request start with /donbeto");
      if (!requestURI.endsWith("variables.js")) {
        requestURI = "/company" + requestURI.substring("/donbeto".length());
        request.setRequestURI(requestURI);
        Logger.debug("The file is not variables.js");
      }
      Logger.debug("Company directory FOUND in path. Add file. Request URI: %s", request.getRequestURI());
    }
    if (requestURI.startsWith("/bariloche")) {
      Logger.debug("Request start with /bariloche");
      if (!requestURI.endsWith("variables.js")) {
        requestURI = "/company" + requestURI.substring("/bariloche".length());
        request.setRequestURI(requestURI);
        Logger.debug("The file is not variables.js");
      }
      Logger.debug("Company directory FOUND in path. Add file. Request URI: %s", request.getRequestURI());
    }
  }

  void changeDomainName(SofiaHTMLServletRequest request) {
    String serverName = request.getServerName();

    if ("cdmx.menu".equals(serverName)) {
      request.setServerName("hayquecomer.com");
    }
  }
}
