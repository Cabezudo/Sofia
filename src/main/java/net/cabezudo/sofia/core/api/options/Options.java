package net.cabezudo.sofia.core.api.options;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.list.ListOptionFactory;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.14
 */
public class Options implements Iterable<Option> {

  protected final Map<String, Option> map = new HashMap<>();

  public Options(HttpServletRequest request) {
    String queryString = request.getQueryString();
    if (queryString == null) {
      return;
    }
    String decodedQueryString;
    decodedQueryString = URLDecoder.decode(queryString, Configuration.getInstance().getEncoding());
    String[] parameters = decodedQueryString.split("&");
    for (String parameter : parameters) {
      Option qp = ListOptionFactory.get(parameter);
      map.put(qp.getName(), qp);
    }
  }

  @Override
  public Iterator<Option> iterator() {
    return map.values().iterator();
  }
}
