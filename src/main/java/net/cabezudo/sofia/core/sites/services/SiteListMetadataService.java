package net.cabezudo.sofia.core.sites.services;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.sites.SiteList;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.ws.servlet.services.ListService;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.08.10
 */
public class SiteListMetadataService extends ListService {

  public SiteListMetadataService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void get() throws ServletException {
    Logger.debug("Run get method in web service %s.", this.getClass().getName());
    try {
      User owner = super.getUser();
      int total = SiteManager.getInstance().getTotal(super.getFilters(), super.getSort(), super.getOffset(), super.getLimit(), owner);
      JSONObject jsonObject = new JSONObject();
      JSONArray jsonArray = new JSONArray();
      jsonObject.add(new JSONPair("filters", getFilters() == null ? "" : getFilters().getOriginalValue()));
      jsonObject.add(new JSONPair("headers", jsonArray));
      jsonObject.add(new JSONPair("totalRecords", total));
      jsonObject.add(new JSONPair("pageSize", super.getLimit() == null ? SiteList.MAX_PAGE_SIZE : super.getLimit().getValue()));

      JSONObject jsonTitleObject;

      jsonTitleObject = new JSONObject();
      jsonTitleObject.add(new JSONPair("title", "Id"));
      jsonArray.add(jsonTitleObject);

      jsonTitleObject = new JSONObject();
      jsonTitleObject.add(new JSONPair("title", "Nombre"));
      jsonArray.add(jsonTitleObject);

      jsonTitleObject = new JSONObject();
      jsonTitleObject.add(new JSONPair("title", "Version"));
      jsonArray.add(jsonTitleObject);

      out.print(jsonObject.toJSON());
    } catch (ClusterException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable", e);
    }
  }
}
