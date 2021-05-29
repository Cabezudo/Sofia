package net.cabezudo.sofia.core.users;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.ws.servlet.services.ListService;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.18
 */
public class UserListMetadataService extends ListService {

  public UserListMetadataService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void get() throws ServletException {
    Logger.debug("Run get method in web service %s.", this.getClass().getName());
    try {
      User owner = super.getUser();
      int total = UserManager.getInstance().getTotal(super.getFilters(), super.getSort(), super.getOffset(), super.getLimit(), owner);
      JSONObject jsonObject = new JSONObject();
      JSONArray jsonArray = new JSONArray();
      jsonObject.add(new JSONPair("filters", getFilters() == null ? "" : getFilters().getOriginalValue()));
      jsonObject.add(new JSONPair("headers", jsonArray));
      jsonObject.add(new JSONPair("totalRecords", total));
      jsonObject.add(new JSONPair("pageSize", super.getLimit() == null ? UserList.MAX_PAGE_SIZE : super.getLimit().getValue()));

      JSONObject jsonTitleObject;

      jsonTitleObject = new JSONObject();
      jsonTitleObject.add(new JSONPair("title", "Id"));
      jsonArray.add(jsonTitleObject);

      jsonTitleObject = new JSONObject();
      jsonTitleObject.add(new JSONPair("title", "Site"));
      jsonArray.add(jsonTitleObject);

      jsonTitleObject = new JSONObject();
      jsonTitleObject.add(new JSONPair("title", "e-mail"));
      jsonArray.add(jsonTitleObject);

      out.print(jsonObject.toJSON());
    } catch (ClusterException e) {
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable", e);
    }
  }
}
