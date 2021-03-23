package net.cabezudo.sofia.core.ws.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.http.url.parser.URLPathTokenizer;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.ws.WebServiceNotFoundException;
import net.cabezudo.sofia.core.ws.WebServicesUniverse;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.13
 */
public class WebServicesServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setHeader("Content-Type", "application/json; charset=utf-8");

    String uri = URLDecoder.decode(request.getRequestURI(), Configuration.getInstance().getEncoding());

    String serverName = request.getServerName();
    if (serverName.startsWith("api.")) {
      uri = "/api" + uri;
    }

    Logger.debug("GET request for: %s.", uri);
    URLTokens tokens = URLPathTokenizer.tokenize(uri);

    try {
      WebServicesUniverse.getInstance().runGET(request, response, tokens);
    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new SofiaRuntimeException(e);
    } catch (WebServiceNotFoundException e) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String uri = request.getRequestURI();

    Logger.debug("POST request for: %s.", uri);
    URLTokens tokens = URLPathTokenizer.tokenize(uri);

    try {
      WebServicesUniverse.getInstance().runPOST(request, response, tokens);
    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new SofiaRuntimeException(e);
    } catch (WebServiceNotFoundException e) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
    }
  }

  @Override
  protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String uri = request.getRequestURI();
    URLTokens tokens = URLPathTokenizer.tokenize(uri);

    try {
      WebServicesUniverse.getInstance().runPUT(request, response, tokens);
    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new SofiaRuntimeException(e);
    } catch (WebServiceNotFoundException e) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
    }
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String uri = request.getRequestURI();

    Logger.debug("DELETE request for: %s.", uri);
    URLTokens tokens = URLPathTokenizer.tokenize(uri);

    try {
      WebServicesUniverse.getInstance().runDELETE(request, response, tokens);
    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new SofiaRuntimeException(e);
    } catch (WebServiceNotFoundException e) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
    }
  }
}
