package net.cabezudo.sofia.core.sic;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSON;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.ws.parser.tokens.Tokens;
import net.cabezudo.sofia.core.ws.responses.ValidationResponse;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.09
 */
public class FormatService extends Service<ValidationResponse> {

  public FormatService(HttpServletRequest request, HttpServletResponse response, Tokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void execute() throws ServletException {
//    try {
    JSONObject jsonPayload;
    try {
      jsonPayload = JSON.parse(getPayload()).toJSONObject();
    } catch (JSONParseException e) {
      throw new ServletException(e);
    }
    String code;
    try {
      code = jsonPayload.getString("code");
    } catch (PropertyNotExistException e) {
      throw new ServletException(e);
    }
    SofiaImageCode sofiaImageCode = new SofiaImageCode(code, true);

    SICCompilerMessages sicCompilerMessages = sofiaImageCode.getCompilerMessages();

    JSONObject jsonResponse = new JSONObject();

    JSONPair jsonTypePair = new JSONPair("type", "FORMATED_CODE");
    jsonResponse.add(jsonTypePair);

    JSONArray jsonMessages = sicCompilerMessages.toJSON();
    JSONPair jsonMessagesPair = new JSONPair("messages", jsonMessages);
    jsonResponse.add(jsonMessagesPair);

    JSONArray jsonTokens = sofiaImageCode.getTokens().toJSON();
    JSONPair jsonTokensPair = new JSONPair("tokens", jsonTokens);
    jsonResponse.add(jsonTokensPair);

    out.print(jsonResponse.toString());
//    } catch (JSONParseException e) {
//      sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
//    }
  }
}
