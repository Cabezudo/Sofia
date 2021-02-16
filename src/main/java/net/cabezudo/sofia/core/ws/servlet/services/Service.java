package net.cabezudo.sofia.core.ws.servlet.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.webusers.WebUserDataManager;
import net.cabezudo.sofia.core.webusers.WebUserDataManager.WebUserData;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.17
 * @param <T>
 */
public abstract class Service<T extends Response> {

  protected final HttpServletRequest request;
  protected final HttpServletResponse response;
  protected final URLTokens tokens;
  private final HttpSession session;
  protected final PrintWriter out;
  private String payload;
  private WebUserData webUserData;

  protected Service(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    this.request = request;
    this.response = response;
    this.tokens = tokens;
    this.session = request.getSession();
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    try {
      out = response.getWriter();
    } catch (IOException e) {
      throw new ServletException(e);
    }
    String requestId = request.getHeader("RequestId");
    response.setHeader("RequestId", requestId);
  }

  public void get() throws ServletException {
    Logger.debug("Service not found using GET method.");
    sendError(HttpServletResponse.SC_NOT_FOUND, "Sofia service not found.");
  }

  public void post() throws ServletException {
    Logger.debug("Service not found using POST method.");
    sendError(HttpServletResponse.SC_NOT_FOUND, "Sofia service not found.");
  }

  public void delete() throws ServletException {
    Logger.debug("Service not found using DELETE method.");
    sendError(HttpServletResponse.SC_NOT_FOUND, "Sofia service not found.");
  }

  public void put() throws ServletException {
    Logger.debug("Service not found using PUT method.");
    sendError(HttpServletResponse.SC_NOT_FOUND, "Sofia service not found.");
  }

  public void options() throws ServletException {
    Logger.debug("Service not found using OPTIONS method.");
    sendError(HttpServletResponse.SC_NOT_FOUND, "Sofia service not found.");
  }

  protected void sendResponse(T response) throws ServletException {
    try {
      String jsonResponse = response.toJSON(getSite(), getWebUserData().getActualLanguage()).toString();
      Logger.debug(jsonResponse);
      out.print(jsonResponse);
    } catch (SQLException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e.getMessage());
    }
  }

  protected void setWebUserData(WebUserData webUserData) {
    request.getSession().setAttribute("webUserData", webUserData);
  }

  protected WebUserData getWebUserData() throws SQLException {
    if (webUserData == null) {
      webUserData = (WebUserData) request.getSession().getAttribute("webUserData");
      if (webUserData == null) {
        WebUserDataManager webUserDataManager = WebUserDataManager.getInstance();
        webUserData = webUserDataManager.get(request);
        setWebUserData(webUserData);
      }
    }
    return webUserData;
  }

  private String readPayload() throws ServletException {
    String body;
    try {
      body = request.getReader().lines().reduce("", (partialBody, line) -> partialBody + (line + "\n"));
    } catch (IOException e) {
      throw new ServletException(e);
    }
    return body;
  }

  protected String getPayload() throws ServletException {
    if (payload == null) {
      payload = readPayload();
    }
    return payload;
  }

  protected void sendError(int error, Throwable cause) throws ServletException {
    if (Environment.getInstance().isDevelopment()) {
      cause.printStackTrace();
    }
    sendError(error, cause.getMessage());
  }

  protected final void sendError(int error, String message) throws ServletException {
    try {
      response.sendError(error, message);
    } catch (IOException e) {
      throw new ServletException(e);
    }
  }

  public final HttpSession getSession() {
    return session;
  }

  protected Site getSite() {
    return (Site) request.getAttribute("site");
  }

  protected User getUser() throws SQLException {
    if (webUserData == null) {
      return null;
    }
    return webUserData.getUser();
  }

  public QueryParameters getQueryParmeters() {
    String queryString = request.getQueryString();
    QueryParameters queryParameters = new QueryParameters();
    if (queryString == null) {
      return queryParameters;
    }
    String decodedQueryString;
    decodedQueryString = URLDecoder.decode(queryString, Configuration.getInstance().getEncoding());
    String[] parameters = decodedQueryString.split("&");
    for (String parameter : parameters) {
      String[] dupla = parameter.split("=");
      queryParameters.put(dupla[0], dupla[1]);
    }
    return queryParameters;
  }
}
