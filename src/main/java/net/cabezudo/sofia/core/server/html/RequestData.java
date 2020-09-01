package net.cabezudo.sofia.core.server.html;

import net.cabezudo.sofia.core.cache.Cache;
import net.cabezudo.sofia.core.cache.CacheManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.01
 */
class RequestData {

  public static final String CACHE_NAME = "requestData";

  public static Cache<String, RequestData> getCache() {
    return CacheManager.getInstance().getCache(RequestData.CACHE_NAME);
  }

  private final String serverName;
  private final String requestURI;

  RequestData(String serverName, String requestURI) {
    this.serverName = serverName;
    this.requestURI = requestURI;
  }

  public String getServerName() {
    return serverName;
  }

  public String getRequestURI() {
    return requestURI;
  }
}
