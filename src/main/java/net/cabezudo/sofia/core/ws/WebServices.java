package net.cabezudo.sofia.core.ws;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.sofia.core.APIEntries;
import net.cabezudo.sofia.core.APIEntry;
import net.cabezudo.sofia.logger.Logger;
import org.eclipse.jetty.http.HttpMethod;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.02.26
 */
abstract class WebServices implements Iterable<WebService> {

  private final Set<WebService> methods = new TreeSet<>();

  void add(APIEntries apiEntries) throws PropertyNotExistException, ClassNotFoundException {
    if (apiEntries == null) {
      return;
    }
    for (APIEntry apiEntry : apiEntries) {
      String path = apiEntry.getPath();
      String className = apiEntry.getClassName();
      if (className.isEmpty()) {
        Logger.debug("Skip %s %s", getMethod(), path);
      } else {
        WebService webService = new WebService(getMethod(), path, className);
        methods.add(webService);
        Logger.debug("Add: %s %s (%s)", getMethod(), path, webService.getClassName());
      }
    }
  }

  abstract HttpMethod getMethod();

  @Override
  public Iterator iterator() {
    return methods.iterator();
  }

}
