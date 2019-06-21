package net.cabezudo.sofia.core;

import java.util.Iterator;
import net.cabezudo.json.JSONable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.16
 * @param <T>
 */
public abstract class EntityList<T> implements JSONable, Iterable<T> {

  public static final long MAX = 200;

  private long total;
  private long offset;
  private long pageSize;

  public void setTotal(long total) {
    this.total = total;
  }

  public long getTotal() {
    return total;
  }

  public void setOffset(long offset) {
    this.offset = offset;
  }

  public long getOffset() {
    return offset;
  }

  public void setPageSize(long pageSize) {
    this.pageSize = pageSize;
  }

  public long getPageSize() {
    return pageSize;
  }

  @Override
  public abstract Iterator<T> iterator();

}
