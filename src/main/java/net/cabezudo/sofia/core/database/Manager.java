package net.cabezudo.sofia.core.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import net.cabezudo.sofia.core.api.options.OptionValue;
import net.cabezudo.sofia.core.api.options.list.Filters;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.02.24
 */
public abstract class Manager {

  protected String getWhere(Filters filter) {
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

  protected void setFiltersValues(Filters filter, PreparedStatement ps) throws SQLException {
    if (filter != null) {
      int i = 1;
      for (OptionValue ov : filter.getValues()) {
        ps.setString(i, "%" + ov.getValue() + "%");
        i++;
      }
    }
  }

}
