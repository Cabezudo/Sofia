package net.cabezudo.sofia.core.creator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
public class Libraries implements Iterable<Library> {

  private final List<Library> list;
  private final Map<String, Library> map;

  public Libraries() {
    this.list = new ArrayList<>();
    this.map = new HashMap<>();
  }

  void add(Libraries libraries) throws LibraryVersionConflictException {
    if (libraries == null) {
      return;
    }
    for (Library library : libraries) {
      add(library);
    }
  }

  void add(Library l) throws LibraryVersionConflictException {
    Library library = map.get(l.getName());
    if (library == null) {
      map.put(l.getName(), l);
      list.add(l);
    } else {
      if (!l.getVersion().equals(library.getVersion())) {
        throw new LibraryVersionConflictException(library, l);
      }
    }
  }

  @Override
  public Iterator<Library> iterator() {
    return list.iterator();
  }

  Lines getCCSLines() {
    Lines lines = new CSSLines();
    for (Library library : list) {
      for (CSSSourceFile file : library.getCascadingStyleSheetFiles()) {
        Line commentLine = new CodeLine("/* " + library + " addeded from " + file.getCaller() + " */");
        lines.add(commentLine);
        for (Line line : file.getLines()) {
          lines.add(line);
        }
      }
    }
    return lines;
  }

  Lines getJavaScriptLines() {
    Lines lines = new JSLines();
    for (Library library : list) {
      for (JSSourceFile file : library.getJavaScritpFiles()) {
        Line commentLine = new CodeLine("// " + library + " addeded from " + file.getCaller());
        lines.add(commentLine);
        for (Line line : file.getJavaScriptLines()) {
          lines.add(line);
        }
      }
    }
    return lines;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Library library : list) {
      sb.append(library.toString()).append('\n');
    }
    return sb.toString();
  }
}
