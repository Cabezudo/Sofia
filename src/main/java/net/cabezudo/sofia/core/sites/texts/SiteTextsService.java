package net.cabezudo.sofia.core.sites.texts;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.company.CompanySubdomainManager;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLToken;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.languages.InvalidTwoLettersCodeException;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.languages.LanguageManager;
import net.cabezudo.sofia.core.ws.servlet.services.Service;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.08.10
 */
public class SiteTextsService extends Service {

  public SiteTextsService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void get() throws ServletException {
    Logger.debug("Run get method in web service.");
    try {
      URLToken pageToken = tokens.getValue("page");
      String page = pageToken.toString().replaceAll("\\.", "/");

      int i = page.indexOf("/", 1);
      if (i > 0) {
        String subdomain = page.substring(1, i);
        Integer companyId = CompanySubdomainManager.getInstance().get(subdomain);

        if (companyId != null) {
          page = page.replace(subdomain, "restaurant");
        }
      }

      URLToken languageToken = tokens.getValue("language");
      String twoLettersCode = languageToken.toString();
      Language language = LanguageManager.getInstance().get(twoLettersCode);

      JSONObject texts = TextManager.get(site, page, language);
      out.print(texts.toJSON());
    } catch (ClusterException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable", e);
    } catch (InvalidTwoLettersCodeException e) {
      sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found", e);
    } catch (JSONParseException | IOException e) {
      sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
    }
  }
}
