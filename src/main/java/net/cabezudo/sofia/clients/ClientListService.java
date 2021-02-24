package net.cabezudo.sofia.clients;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserNotExistException;
import net.cabezudo.sofia.core.ws.servlet.services.ListService;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.13
 */
public class ClientListService extends ListService {

  public ClientListService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException {
    super(request, response, tokens);
  }

  @Override
  public void get() throws ServletException {
    try {
      User owner = webUserData.getUser();

      ClientList list = ClientManager.getInstance().list(getFilters(), getSort(), getOffset(), getLimit(), owner);

      if (getOffset() == null) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonObject.add(new JSONPair("filters", getFilters() == null ? "" : getFilters().getOriginalValue()));
        jsonObject.add(new JSONPair("headers", jsonArray));
        jsonObject.add(new JSONPair("totalRecords", list.getTotal()));
        jsonObject.add(new JSONPair("pageSize", list.getPageSize()));

        JSONObject jsonTitleObject;

        jsonTitleObject = new JSONObject();
        jsonTitleObject.add(new JSONPair("title", "Id"));
        jsonArray.add(jsonTitleObject);

        jsonTitleObject = new JSONObject();
        jsonTitleObject.add(new JSONPair("title", "Nombre"));
        jsonArray.add(jsonTitleObject);

        jsonTitleObject = new JSONObject();
        jsonTitleObject.add(new JSONPair("title", "Apellido"));
        jsonArray.add(jsonTitleObject);

        jsonTitleObject = new JSONObject();
        jsonTitleObject.add(new JSONPair("title", "Correo"));
        jsonArray.add(jsonTitleObject);

        out.print(jsonObject.toJSON());
      } else {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonObject.add(new JSONPair("list", jsonArray));

        long row = list.getOffset();

        for (Client client : list) {
          JSONObject item = new JSONObject();
          item.add(new JSONPair("row", row));
          JSONArray fields = new JSONArray();
          fields.add(client.getId());
          fields.add(client.getName());
          fields.add(client.getLastName());
          if (client.getEMails().getPrimaryEMail() != null) {
            fields.add(client.getEMails().getPrimaryEMail().getAddress());
          } else {
            fields.add("");
          }
          item.add(new JSONPair("fields", fields));

          jsonArray.add(item);
          row++;
        }

        out.print(jsonObject.toJSON());
      }
    } catch (ClusterException e) {
      Logger.severe(e);
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e.getMessage());
    } catch (UserNotExistException e) {
      sendError(HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
    }
  }
}
