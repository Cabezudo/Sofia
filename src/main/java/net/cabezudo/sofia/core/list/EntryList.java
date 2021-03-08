package net.cabezudo.sofia.core.list;

import net.cabezudo.json.JSONable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.16
 * @param <T>
 */
public abstract class EntryList<T> implements JSONable, Iterable<T> {

  public static final int MAX_PAGE_SIZE = 200;

  private int total;
  private final int offset;
  private final int pageSize;

  public EntryList(int offset, int pageSize) {
    this.offset = offset;
    this.pageSize = pageSize;
  }

  public EntryList(int offset) {
    this.offset = offset;
    this.pageSize = MAX_PAGE_SIZE;
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
  public String toJSON() {
    return toJSONTree().toString();
  }

}
