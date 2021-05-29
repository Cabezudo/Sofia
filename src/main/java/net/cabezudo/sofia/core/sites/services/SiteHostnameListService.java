package net.cabezudo.sofia.core.sites.services;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.InvalidPathParameterException;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLToken;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.sites.domainname.DomainNameList;
import net.cabezudo.sofia.core.sites.domainname.DomainNameManager;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.ws.servlet.services.ListService;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.24.10
 *
 */
public class SiteHostnameListService extends ListService {

  private final int MAX_ITEMS = 200;

  public SiteHostnameListService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void get() throws ServletException {
    int siteId;
    try {
      User owner = super.getUser();

      URLToken token = tokens.getValue("siteId");
      try {
        siteId = token.toInteger();
      } catch (InvalidPathParameterException e) {
        sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found", e);
        return;
      }

      Site site = SiteManager.getInstance().getById(siteId);
      if (site == null) {
        sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
        return;
      }
      if (getOffset() == null) {
        int total = SiteManager.getInstance().getHostsTotal(site, super.getFilters(), super.getSort(), super.getOffset(), super.getLimit(), owner);

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonObject.add(new JSONPair("filters", getFilters() == null ? "" : getFilters().getOriginalValue()));
        jsonObject.add(new JSONPair("headers", jsonArray));
        jsonObject.add(new JSONPair("totalRecords", total));
        jsonObject.add(new JSONPair("pageSize", super.getLimit() == null ? MAX_ITEMS : super.getLimit().getValue()));

        JSONObject jsonTitleObject;

        jsonTitleObject = new JSONObject();
        jsonTitleObject.add(new JSONPair("title", "Id"));
        jsonArray.add(jsonTitleObject);

        jsonTitleObject = new JSONObject();
        jsonTitleObject.add(new JSONPair("title", "Nombre"));
        jsonArray.add(jsonTitleObject);

        out.print(jsonObject.toJSON());
      } else {
        DomainNameList list = DomainNameManager.getInstance().listDomainNames(site, super.getFilters(), super.getSort(), super.getOffset(), super.getLimit(), owner);
        out.print(list.toJSON());
      }
    } catch (ClusterException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable", e);
    }
  }
}
