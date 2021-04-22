package net.cabezudo.sofia.geography;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AdministrativeDivisions implements Iterable<AdministrativeDivision> {

  private final List<AdministrativeDivision> list = new ArrayList<>();

  @Override
  public Iterator<AdministrativeDivision> iterator() {
    return list.iterator();
  }

  int size() {
    return list.size();
  }
}
