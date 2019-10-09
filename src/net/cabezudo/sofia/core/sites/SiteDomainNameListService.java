package net.cabezudo.sofia.core.sites;

import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.system.SystemMonitor;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.ws.parser.tokens.Token;
import net.cabezudo.sofia.core.ws.parser.tokens.Tokens;
import net.cabezudo.sofia.core.ws.servlet.services.ListService;
import net.cabezudo.sofia.core.sites.domainname.DomainNameList;
import net.cabezudo.sofia.core.sites.domainname.DomainNameManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.24.10
 *
 */
public class SiteDomainNameListService extends ListService {

  private final int MAX_ITEMS = 200;

  private final int siteId;

  public SiteDomainNameListService(HttpServletRequest request, HttpServletResponse response, Tokens tokens) throws ServletException {
    super(request, response);
    Token token = tokens.getValue("siteId");
    siteId = token.toInteger();
  }

  @Override
  public void execute() throws ServletException {
    try {
      Site site = SiteManager.getInstance().getById(siteId);
      if (site == null) {
        sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
        return;
      }
      User owner = super.getUser();
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
    } catch (SQLException e) {
      SystemMonitor.log(e);
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service unavailable");
    }
  }
}
