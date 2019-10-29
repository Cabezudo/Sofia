package net.cabezudo.sofia.core.creator;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.10.26
 */
public class Libraries implements Iterable<Library> {

  private final Map<String, Library> map;

  public Libraries() {
    this.map = new TreeMap<>();
  }

  @Override
  public Iterator<Library> iterator() {
    return map.values().iterator();
  }

  void add(Library library) throws LibraryVersionException {
    Library l = map.get(library.getName());
    if (l == null) {
      map.put(library.getName(), library);
    } else {
      if (!l.getVersion().equals(library.getVersion())) {
        throw new LibraryVersionException("try to replace a library " + l + " with library " + library + ".");
      }
    }
  }

}
