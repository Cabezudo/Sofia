package net.cabezudo.sofia.names;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.languages.InvalidTwoLettersCodeException;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.languages.LanguageManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.21
 */
public abstract class InternationalizedNameManager {

  private final String databaseName;
  private final String tableName;

  public InternationalizedNameManager(String databaseName, String tableName) {
    this.databaseName = databaseName;
    this.tableName = tableName;
  }

  public InternationalizedName get(int id, Language language) throws ClusterException, InvalidTwoLettersCodeException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, id, language);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public InternationalizedName get(Connection connection, int id, Language language) throws ClusterException, InvalidTwoLettersCodeException {
    String query
            = "SELECT id, value, language "
            + "FROM " + databaseName + "." + tableName + " "
            + "WHERE id = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, id);
      rs = ClusterManager.getInstance().executeQuery(ps);
      InternationalizedName name = null;
      InternationalizedName englishName = null;
      Language en = LanguageManager.getInstance().get("en");
      while (rs.next()) {
        int languageId = rs.getInt("language");
        name = new InternationalizedName(rs.getInt("id"), language, rs.getString("value"));
        if (languageId == language.getId()) {
          return name;
        }
        if (languageId == en.getId()) {
          englishName = name;
        }
      }
      if (englishName != null) {
        return englishName;
      }
      return name;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }
}
