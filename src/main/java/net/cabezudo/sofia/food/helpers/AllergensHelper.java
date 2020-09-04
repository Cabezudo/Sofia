package net.cabezudo.sofia.food.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.09.04
 */
public class AllergensHelper implements Iterable<AllergenHelper> {

  private List<AllergenHelper> list = new ArrayList<>();

  void add(AllergenHelper allergen) {
    list.add(allergen);
  }

  @Override
  public Iterator<AllergenHelper> iterator() {
    return list.iterator();
  }

}
