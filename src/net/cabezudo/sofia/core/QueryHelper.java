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

  public static String getOrderString(Sort sort, String defaultValue, String... validOptions) {
    String sqlSort = " ORDER BY " + defaultValue;
    if (sort != null) {
      List<OptionValue> sorters = sort.getValues();
      Set<String> set = new TreeSet();
      if (sorters.size() > 0) {
        sqlSort = " ORDER BY ";
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
        sqlSort = Utils.chop(sqlSort, 2);
      }
    }
    return sqlSort;
  }
}
