package net.cabezudo.sofia.core.ws.servlet.services;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.api.options.list.Filters;
import net.cabezudo.sofia.core.api.options.list.Limit;
import net.cabezudo.sofia.core.api.options.list.ListOptions;
import net.cabezudo.sofia.core.api.options.list.Offset;
import net.cabezudo.sofia.core.api.options.list.Sort;
import net.cabezudo.sofia.core.ws.parser.tokens.Tokens;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.13
 */
public abstract class ListService extends Service {

  private final ListOptions listOptions;

  private Filters filters;
  private Sort sort;
  private final Offset offset; // Is final because not persist and only matter the request value
  private Limit limit;

  public ListService(HttpServletRequest request, HttpServletResponse response, Tokens tokens) throws ServletException {
    super(request, response, tokens);

    // List the clients. GET /api/v1/clients?sort=+name,-lastName&fields=name,lastName&offset=10&limit=50
    listOptions = new ListOptions(request);

    // Filters must be persist in the session
    filters = listOptions.getFilters();
    if (filters == null) {
      filters = (Filters) getSession().getAttribute("clientListFilters");
    }
    // Sort must be persist in the session
    sort = listOptions.getSort();
    if (sort == null) {
      sort = (Sort) getSession().getAttribute("clientListSort");
    }
    limit = listOptions.getLimit();
    if (limit == null) {
      limit = (Limit) getSession().getAttribute("clientListLimit");
    }

    // Offset must not persist in session because the empty option is uset to the headers list
    offset = listOptions.getOffset();

    if (filters != null) {
      getSession().setAttribute("clientListFilters", filters);
    }
  }

  public ListOptions getListOptions() {
    return listOptions;
  }

  public Filters getFilters() {
    return filters;
  }

  public Sort getSort() {
    return sort;
  }

  public Offset getOffset() {
    return offset;
  }

  public Limit getLimit() {
    return limit;
  }
}
