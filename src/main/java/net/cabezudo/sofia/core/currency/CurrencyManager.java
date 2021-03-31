package net.cabezudo.sofia.core.currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.11.23
 */
public class CurrencyManager {

  private static final CurrencyManager INSTANCE = new CurrencyManager();

  private CurrencyManager() {
    // Nothing to do here. Jus protect the instance
  }

  public static CurrencyManager getInstance() {
    return INSTANCE;
  }

  public Currency get(String currencyCode) throws InvalidCurrencyCodeException, ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, currencyCode);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Currency get(Connection connection, String currencyCode) throws ClusterException, InvalidCurrencyCodeException {
    String query
            = "SELECT id, currencyCode "
            + "FROM " + CurrenciesTable.DATABASE_NAME + "." + CurrenciesTable.NAME + " "
            + "WHERE currencyCode = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, currencyCode);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        return new Currency(rs.getInt("id"), rs.getString("currencyCode"));
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    throw new InvalidCurrencyCodeException(currencyCode);
  }

  public Currency add(Connection connection, String currencyCode) throws ClusterException {
    String query = "INSERT INTO " + CurrenciesTable.DATABASE_NAME + "." + CurrenciesTable.NAME + " (currencyCode) VALUES (?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setString(1, currencyCode);
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new Currency(id, currencyCode);
      }
      throw new SofiaRuntimeException("Can't get the generated key");
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }
}
