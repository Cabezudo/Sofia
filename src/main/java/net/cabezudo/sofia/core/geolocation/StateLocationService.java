package net.cabezudo.sofia.core.geolocation;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLToken;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.languages.InvalidTwoLettersCodeException;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.languages.LanguageManager;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.QueryParameters;
import net.cabezudo.sofia.core.ws.servlet.services.Service;
import net.cabezudo.sofia.geography.AdministrativeDivision;
import net.cabezudo.sofia.geography.AdministrativeDivisionManager;
import net.cabezudo.sofia.geography.AdministrativeDivisionType;
import net.cabezudo.sofia.geography.InvalidPolygonDataException;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.18
 */
public class StateLocationService extends Service {

  public StateLocationService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void get() throws ServletException {

    URLToken longitudeToken = tokens.getValue("longitude");
    URLToken latitudeToken = tokens.getValue("latitude");
    QueryParameters queryParameters = getQueryParmeters();
    String languageOnQueryParameters = queryParameters.get("language");

    try {
      Longitude longitude = new Longitude(longitudeToken.toString());
      Latitude latitude = new Latitude(latitudeToken.toString());
      Language language;

      if (languageOnQueryParameters == null) {
        language = webUserData.getActualLanguage();
      } else {
        language = LanguageManager.getInstance().get(languageOnQueryParameters);
      }

      AdministrativeDivision ad;
      ad = AdministrativeDivisionManager.getInstance().get(longitude, latitude, AdministrativeDivisionType.STATE);

      JSONObject data = new JSONObject();
      data.add(new JSONPair("longitude", longitude.toDouble()));
      data.add(new JSONPair("latitude", latitude.toDouble()));
      data.add(new JSONPair("code", ad == null ? null : ad.getCode()));
      data.add(new JSONPair("name", ad == null ? null : ad.getName(language).toJSONTree()));

      sendResponse(new Response(Response.Status.OK, Response.Type.READ, data, "state.found"));

    } catch (InvalidLongitudeException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.VALIDATION, "longitude.notValid", longitudeToken.toString()));
      return;
    } catch (InvalidLatitudeException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.VALIDATION, "latitude.notValid", latitudeToken.toString()));
      return;
    } catch (InvalidTwoLettersCodeException e) {
      sendResponse(new Response(Response.Status.ERROR, Response.Type.VALIDATION, "language.notValid", languageOnQueryParameters));
    } catch (JSONParseException | IOException | ClusterException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable", e);
    } catch (InvalidPolygonDataException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable", e);
      Logger.severe(e);
    }
  }
}
