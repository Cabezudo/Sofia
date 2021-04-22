package net.cabezudo.sofia.core.geolocation;

import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.InvalidPathParameterException;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLToken;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.18
 */
public class LatitudeValidatorService extends Service {

  public LatitudeValidatorService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void get() throws ServletException {
    URLToken token = tokens.getValue("value");
    try {
      BigDecimal latitude = token.toBigDecimal();
      Latitude.validate(latitude);
    } catch (InvalidPathParameterException | InvalidLatitudeException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.VALIDATION, "latitude.notValid", token.toString()));
      return;
    }
    sendResponse(new Response(Response.Status.OK, Response.Type.VALIDATION, "latitude.ok"));
  }
}
