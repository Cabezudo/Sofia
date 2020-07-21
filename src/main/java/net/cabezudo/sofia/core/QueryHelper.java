package net.cabezudo.sofia.core;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.cabezudo.sofia.core.api.options.OptionValue;
import net.cabezudo.sofia.core.api.options.list.Sort;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.08.12
 */
public class QueryHelper {

  private QueryHelper() {
    // Nothing to do here. Utility classes should not have public constructors.
  }

  public static String getOrderString(Sort sort, String defaultValue, String... validOptions) {
    String sqlSort = " ORDER BY " + defaultValue;
    if (sort == null) {
      return sqlSort;
    }
    List<OptionValue> sorters = sort.getValues();
    if (sorters.size() > 0) {
      return getNewSort(sorters, validOptions);
    }
    return sqlSort;
  }

  private static String getNewSort(List<OptionValue> sorters, String... validOptions) {
    String sqlSort = " ORDER BY ";
    Set<String> set = new TreeSet();
    for (OptionValue sorter : sorters) {
      String name = sorter.getValue().toString();
      if (set.contains(name)) {
        continue;
      }
      List<String> validOptionsList = Arrays.asList(validOptions);
      if (validOptionsList.contains(name)) {
        sqlSort += sorter.getValue() + (sorter.isNegative() ? " DESC" : "") + ", ";
        set.add(name);
      }
    }
    return Utils.chop(sqlSort, 2);
  }
}
