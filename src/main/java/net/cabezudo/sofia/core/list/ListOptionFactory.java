package net.cabezudo.sofia.core.list;

import net.cabezudo.sofia.core.api.options.Option;
import net.cabezudo.sofia.core.ws.servlet.services.InvalidQueryParameterName;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.14
 */
public class ListOptionFactory {

  private ListOptionFactory() {
    // Utility classes should not have public constructors.
  }

  public static Option get(String parameter) {
    String[] dupla = parameter.split("=");
    switch (dupla[0]) {
      case Option.FILTERS:
        if (dupla.length == 2) {
          return new Filters(dupla[1]);
        }
        return new Filters("");
      case Option.SORT:
        if (dupla.length == 2) {
          return new Sort(dupla[1]);
        }
        return new Sort("");
      case Option.FIELDS:
        if (dupla.length == 2) {
          return new Fields(dupla[1]);
        }
        return new Fields("");
      case Option.OFFSET:
        if (dupla.length == 2) {
          Integer value = Integer.parseInt(dupla[1]);
          return new Offset(value);
        }
        return null;
      case Option.LIMIT:
        if (dupla.length == 2) {
          Integer value = Integer.parseInt(dupla[1]);
          return new Limit(value);
        }
        return null;

      default:
        throw new InvalidQueryParameterName(dupla[0]);
    }
  }
}
