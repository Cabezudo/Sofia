package net.cabezudo.sofia.core.list;

import jakarta.servlet.http.HttpServletRequest;
import net.cabezudo.sofia.core.api.options.Option;
import net.cabezudo.sofia.core.api.options.Options;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.14
 */
public class ListOptions extends Options {

  public ListOptions(HttpServletRequest request) {
    super(request);
  }

  public Filters getFilters() {
    return (Filters) super.map.get(Option.FILTERS);
  }

  public Sort getSort() {
    return (Sort) super.map.get(Option.SORT);
  }

  public Offset getOffset() {
    return (Offset) super.map.get(Option.OFFSET);
  }

  public Limit getLimit() {
    return (Limit) super.map.get(Option.LIMIT);
  }
}
