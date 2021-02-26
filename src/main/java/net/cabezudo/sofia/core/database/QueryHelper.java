package net.cabezudo.sofia.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.api.options.OptionValue;
import net.cabezudo.sofia.core.api.options.list.Filters;
import net.cabezudo.sofia.core.api.options.list.Limit;
import net.cabezudo.sofia.core.api.options.list.Offset;
import net.cabezudo.sofia.core.api.options.list.Sort;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.08.12
 */
public class QueryHelper {

  private QueryHelper() {
    // Nothing to do here. Utility classes should not have public constructors.
  }

  public static String getOrderString(Sort sort, String defaultValue, ValidSortColumns validSortColumns) {
    String sqlSort = " ORDER BY " + defaultValue;
    if (sort == null) {
      return sqlSort;
    }
    List<OptionValue> sorters = sort.getValues();
    if (sorters.size() > 0) {
      return getNewSort(sorters, validSortColumns);
    }
    return sqlSort;
  }

  private static String getNewSort(List<OptionValue> sorters, ValidSortColumns validSortColumns) {
    String sqlSort = " ORDER BY ";
    Set<String> set = new TreeSet();
    for (OptionValue sorter : sorters) {
      String name = sorter.getValue().toString();
      if (set.contains(name)) {
        continue;
      }
      List<String> validOptionsList = Arrays.asList(validSortColumns.toArray());
      if (validOptionsList.contains(name)) {
        sqlSort += sorter.getValue() + (sorter.isNegative() ? " DESC" : "") + ", ";
        set.add(name);
      }
    }
    return Utils.chop(sqlSort, 2);
  }

  public static String getWhere(Filters filter) {
    StringBuilder sb = new StringBuilder(" WHERE 1 = 1");
    if (filter != null) {
      // TODO Use this to create any WHERE
      List<OptionValue> values = filter.getValues();
      values.forEach(value -> {
        if (value.isPositive()) {
          sb.append(" AND (name LIKE ?)");
        } else {
          sb.append(" AND name NOT LIKE ?");
        }
      });
    }
    return sb.toString();
  }

  public static void setFiltersValues(Filters filter, PreparedStatement ps) throws SQLException {
    if (filter != null) {
      int i = 1;
      for (OptionValue ov : filter.getValues()) {
        ps.setString(i, "%" + ov.getValue() + "%");
        i++;
      }
    }
  }

  public static int getTotal(
          String databaseName, String tableName, int sqlLimitValue, String defaultOrderColumnName,
          ValidSortColumns validSortColumns, Filters filters, Sort sort, Offset offset, Limit limit, User owner)
          throws ClusterException {
    Logger.fine("Get total of records using filters and limits");

    try (Connection connection = Database.getConnection()) {
      String where = QueryHelper.getWhere(filters);

      long sqlOffsetValue = 0;
      if (offset != null) {
        sqlOffsetValue = offset.getValue();
      }
      if (limit != null) {
        sqlLimitValue = limit.getValue();
      }
      String sqlSort = QueryHelper.getOrderString(sort, defaultOrderColumnName, validSortColumns);
      String sqlLimit = " LIMIT " + sqlOffsetValue + ", " + sqlLimitValue;

      String query = "SELECT count(*) AS total FROM " + databaseName + "." + tableName + where + sqlLimit;
      ResultSet rs = null;
      try (PreparedStatement ps = connection.prepareStatement(query);) {
        QueryHelper.setFiltersValues(filters, ps);
        rs = ClusterManager.getInstance().executeQuery(ps);
        if (rs.next()) {
          return rs.getInt("total");
        } else {
          throw new SofiaRuntimeException("Column expected.");
        }
      } finally {
        ClusterManager.getInstance().close(rs);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }
}
