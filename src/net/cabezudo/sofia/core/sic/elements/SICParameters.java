package net.cabezudo.sofia.core.sic.elements;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class SICParameters implements Iterable<SICElement> {

  private final Queue<SICElement> queue = new LinkedList<>();

  public SICElement consume() {
    SICElement parameter = queue.poll();
    return parameter;
  }

  @Override
  public Iterator<SICElement> iterator() {
    return queue.iterator();
  }

  @Override
  public void forEach(Consumer<? super SICElement> action) {
    queue.forEach(action);
  }

  public int size() {
    return queue.size();
  }

  public void add(SICElement sicParameterOrFunction) {
    queue.add(sicParameterOrFunction);
  }
}
