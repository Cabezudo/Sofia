package net.cabezudo.sofia.core.sic.elements;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class SICParameters implements Iterable<SICParameterOrFunction> {

  private final Queue<SICParameterOrFunction> queue = new LinkedList<>();

  public SICParameterOrFunction consume() {
    SICParameterOrFunction parameter = queue.poll();
    return parameter;
  }

  @Override
  public Iterator<SICParameterOrFunction> iterator() {
    return queue.iterator();
  }

  @Override
  public void forEach(Consumer<? super SICParameterOrFunction> action) {
    queue.forEach(action);
  }

  public int size() {
    return queue.size();
  }

  public void add(SICParameterOrFunction sicParameterOrFunction) {
    queue.add(sicParameterOrFunction);
  }
}
