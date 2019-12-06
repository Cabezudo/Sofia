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

  void add(Libraries libraries) {
    for (Library library : libraries) {
      add(library);
    }
  }

  void add(Library library) {
    list.add(library);
  }

  @Override
  public Iterator<Library> iterator() {
    return list.iterator();
  }

  Lines getCCSLines() {
    Lines lines = new Lines();
    for (Library library : list) {
      for (CSSSourceFile file : library.getCSSFiles()) {
        Line commentLine = new CodeLine("/* " + library + " addeded from " + file.getCaller() + " */");
        lines.add(commentLine);
        for (Line line : file.getLines()) {
          lines.add(line);
        }
      }
    }
    return lines;
  }

  Lines getJSLines() {
    Lines lines = new Lines();
    for (Library library : list) {
      System.out.println(library);
      for (JSSourceFile file : library.getJSFiles()) {
        Line commentLine = new CodeLine("// " + library + " addeded from " + file.getCaller());
        lines.add(commentLine);
        for (Line line : file.getLines()) {
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
