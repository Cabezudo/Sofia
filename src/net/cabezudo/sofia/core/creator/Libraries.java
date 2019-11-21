package net.cabezudo.sofia.core.creator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
public class Libraries implements Iterable<Library> {

  private final List<Library> list;

  public Libraries() {
    this.list = new ArrayList<>();
  }

  void add(Library library) {
    list.add(library);
  }

  @Override
  public Iterator<Library> iterator() {
    return list.iterator();
  }
}
