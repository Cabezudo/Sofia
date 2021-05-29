package net.cabezudo.sofia.core.session.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.09
 */
public class AddMessageService extends Service {

  public AddMessageService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void post() throws ServletException {
    try {
      String payload = getPayload();
      JSONObject jsonData = JSON.parse(payload).toJSONObject();
      getSession().setAttribute("message", jsonData.toString());

      sendResponse(new Response(Response.Status.OK, Response.Type.CREATE, "messages.sent"));
    } catch (JSONParseException e) {
      sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), e);
    }
  }
}
