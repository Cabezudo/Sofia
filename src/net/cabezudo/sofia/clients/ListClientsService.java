package net.cabezudo.sofia.clients;

import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.api.options.list.Filters;
import net.cabezudo.sofia.core.api.options.list.Limit;
import net.cabezudo.sofia.core.api.options.list.ListOptions;
import net.cabezudo.sofia.core.api.options.list.Offset;
import net.cabezudo.sofia.core.api.options.list.Sort;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserNotExistException;
import net.cabezudo.sofia.core.users.autentication.NotLoggedException;
import net.cabezudo.sofia.core.users.authorization.AuthorizationManager;
import net.cabezudo.sofia.core.webusers.WebUserDataManager.ClientData;
import net.cabezudo.sofia.core.ws.responses.NotAuthenticatedMessage;
import net.cabezudo.sofia.core.ws.responses.NotAuthenticatedResponse;
import net.cabezudo.sofia.core.ws.servlet.services.Service;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.13
 */
public class ListClientsService extends Service {

  private final ListOptions listOptions;
  private final HttpSession session;

  private Filters filters;
  private Sort sort;
  private final Offset offset; // Is final because not persist and only matter the request value
  private Limit limit;

  public ListClientsService(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    super(request, response);
    session = request.getSession();

    // List the clients. GET /api/v1/clients?sort=+name,-lastName&fields=name,lastName&offset=10&limit=50
    listOptions = new ListOptions(request);

    // Filters must be persist in the session
    filters = listOptions.getFilters();
    if (filters == null) {
      filters = (Filters) session.getAttribute("clientListFilters");
    }
    // Sort must be persist in the session
    sort = listOptions.getSort();
    if (sort == null) {
      sort = (Sort) session.getAttribute("clientListSort");
    }
    limit = listOptions.getLimit();
    if (limit == null) {
      limit = (Limit) session.getAttribute("clientListLimit");
    }

    // Offset must not persist in session because the empty option is uset to the headers list
    offset = listOptions.getOffset();

    if (filters != null) {
      session.setAttribute("clientListFilters", filters);
    }
  }

  @Override
  public void execute() throws ServletException {

    ClientData clientData;
    try {
      clientData = getClientData();
    } catch (SQLException e) {
      e.printStackTrace();
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e.getMessage());
      return;
    }

    try {
      User owner = clientData.getUser();
      AuthorizationManager.getInstance().hasAuthorization(ListClientsService.class, owner);

      ClientList list = ClientManager.getInstance().list(filters, sort, offset, limit, owner);

      if (offset == null) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonObject.add(new JSONPair("filters", filters == null ? "" : filters.getOriginalValue()));
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
    } catch (SQLException e) {
      e.printStackTrace();
      sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e.getMessage());
    } catch (UserNotExistException e) {
      sendError(HttpServletResponse.SC_PRECONDITION_FAILED, e.getMessage());
    } catch (NotLoggedException e) {
      sendResponse(new NotAuthenticatedResponse(new NotAuthenticatedMessage("user.notLogged")));
    }
  }
}
