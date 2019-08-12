package net.cabezudo.sofia.core;

import java.util.Iterator;
import net.cabezudo.json.JSONable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.16
 * @param <T>
 */
public abstract class EntityList<T> implements JSONable, Iterable<T> {

  public static final int MAX = 200;

  private int total;
  private final int offset;
  private final int pageSize;

  public EntityList(int offset, int pageSize) {
    this.offset = offset;
    this.pageSize = pageSize;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getTotal() {
    return total;
  }

  public int getOffset() {
    return offset;
  }

  public int getPageSize() {
    return pageSize;
  }

  @Override
  public abstract Iterator<T> iterator();

}
