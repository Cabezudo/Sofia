package net.cabezudo.sofia.core.sic;

import java.nio.file.Path;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSON;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.ws.responses.ValidationResponse;
import net.cabezudo.sofia.core.ws.servlet.services.Service;
import net.cabezudo.sofia.sic.Message;
import net.cabezudo.sofia.sic.Messages;
import net.cabezudo.sofia.sic.SICTokens;
import net.cabezudo.sofia.sic.SofiaImageCode;
import net.cabezudo.sofia.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.sic.elements.SICUnexpectedEndOfCodeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.09
 */
public class CompilerService extends Service<ValidationResponse> {

  public CompilerService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void execute() throws ServletException {
    Site site = (Site) request.getAttribute("site");

    JSONObject jsonPayload;
    try {
      jsonPayload = JSON.parse(getPayload()).toJSONObject();
    } catch (JSONParseException e) {
      throw new ServletException(e);
    }
    String code;
    try {
      code = jsonPayload.getString("code");
      Utils.consoleOutLn(code);
    } catch (PropertyNotExistException e) {
      throw new ServletException(e);
    }

    Path basePath = site.getVersionedSourcesPath();
    JSONObject jsonResponse = new JSONObject();
    Messages messages = new Messages();
    SofiaImageCode sofiaImageCode = new SofiaImageCode(basePath, code);
    try {
      sofiaImageCode.parse();
      sofiaImageCode.compile();
    } catch (SICCompileTimeException e) {
      messages.add(new Message(e.getMessage(), e.getPosition()));
    } catch (SICUnexpectedEndOfCodeException e) {
      messages.add(new Message(e.getMessage()));
    }

    JSONArray jsonMessages = new JSONArray();
    for (Message message : messages) {
      JSONObject jsonMessage = new JSONObject();

      JSONPair jsonPairMessage = new JSONPair("message", message.getText());
      jsonMessage.add(jsonPairMessage);

      JSONObject jsonPosition = new JSONObject();
      jsonPosition.add(new JSONPair("line", message.getPosition().getLine()));
      jsonPosition.add(new JSONPair("row", message.getPosition().getRow()));
      JSONPair jsonPairPosition = new JSONPair("position", jsonPosition);
      jsonMessage.add(jsonPairPosition);

      jsonMessages.add(jsonMessage);
    }

    JSONPair jsonTypePair = new JSONPair("type", "CODE");
    jsonResponse.add(jsonTypePair);

    JSONPair jsonMessagesPair = new JSONPair("messages", jsonMessages);
    jsonResponse.add(jsonMessagesPair);

    SICTokens sicTokens = sofiaImageCode.getTokens();

    JSONArray jsonTokens = new JSONArray();
    sicTokens.forEach(token -> {

      JSONObject jsonSICToken = new JSONObject();
      String tokenValue;
      if (token.isNewLine()) {
        tokenValue = "\\n";
      } else {
        tokenValue = token.getValue();
      }

      JSONPair jsonValue = new JSONPair("value", tokenValue);
      jsonSICToken.add(jsonValue);
      JSONPair jsonClass;
      if (token.isInvalidClass()) {
        jsonClass = new JSONPair("class", "none");
      } else {
        jsonClass = new JSONPair("class", token.getCSSClass());
      }
      jsonSICToken.add(jsonClass);
      JSONPair jsonError = new JSONPair("error", token.isError());
      jsonSICToken.add(jsonError);

      jsonTokens.add(jsonSICToken);
    });
    JSONPair jsonTokensPair = new JSONPair("tokens", jsonTokens);
    jsonResponse.add(jsonTokensPair);

    out.print(jsonResponse.toString());
  }
}
