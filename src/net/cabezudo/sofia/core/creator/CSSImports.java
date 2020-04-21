package net.cabezudo.sofia.core.creator;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
public class CSSImports implements Iterable<CSSImport> {

  private final Set<CSSImport> set;

  public CSSImports() {
    this.set = new TreeSet<>();
  }

  void add(CSSImport i) {
    set.add(i);
  }

  void add(CSSImports imports) {
    if (imports == null) {
      return;
    }
    for (CSSImport cssImport : imports) {
      set.add(cssImport);
    }
  }

  @Override
  public Iterator<CSSImport> iterator() {
    return set.iterator();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (CSSImport cssImport : set) {
      sb.append(cssImport.toString()).append('\n');
    }
    return sb.toString();
  }

  String getImports() {
    return toString();
  }
}
