package net.cabezudo.sofia.core.catalogs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.languages.LanguagesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.31
 * @param <T>
 */
public abstract class InternationalizedCatalogManager<T extends InternationalizedCatalogEntry> {

  private final String catalogDatabaseName;
  private final String catalogTableName;
  private final String wordsDatabaseName;
  private final String wordsTableName;

  protected InternationalizedCatalogManager(String catalogDatabaseName, String catalogTableName, String wordsDatabaseName, String wordsTableName) {
    this.catalogDatabaseName = catalogDatabaseName;
    this.catalogTableName = catalogTableName;
    this.wordsDatabaseName = wordsDatabaseName;
    this.wordsTableName = wordsTableName;
  }

  public T add(Language language, String value) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return add(connection, language, value);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public T add(Connection connection, Language language, String value) throws ClusterException {
    String query = "INSERT INTO " + catalogDatabaseName + "." + catalogTableName + " () VALUES ()";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return (T) addWordEntry(connection, id, language, value);
      }
      throw new SofiaRuntimeException("Can't get the generated key");
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public InternationalizedCatalogEntry add(Connection connection, InternationalizedCatalogEntry entry, Language language, String value) throws ClusterException {
    return addWordEntry(connection, entry.getId(), language, value);
  }

  private InternationalizedCatalogEntry addWordEntry(Connection connection, int targetId, Language language, String value) throws ClusterException {
    String query = "INSERT INTO " + wordsDatabaseName + "." + wordsTableName + " (id, language, value) VALUES (?, ?, ?)";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setInt(1, targetId);
      ps.setInt(2, language.getId());
      ps.setString(3, value);
      ClusterManager.getInstance().executeUpdate(ps);
      return new InternationalizedCatalogEntry(targetId, language, value);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public T get(int id) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, id);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public T get(Connection connection, int id) throws ClusterException {
    String query
            = "SELECT w.value AS value, l.id AS languageId, l.twoLettersCode AS twoLettersCode "
            + "FROM " + catalogDatabaseName + "." + catalogTableName + " AS c "
            + "LEFT JOIN " + wordsDatabaseName + "." + wordsTableName + " AS w ON c.id = w.id "
            + "LEFT JOIN " + LanguagesTable.DATABASE + "." + LanguagesTable.NAME + " AS l ON w.language = l.id "
            + "WHERE id = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, id);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        int languageId = rs.getInt("languageId");
        String twoLettersCode = rs.getString("twoLettersCode");
        String value = rs.getString("value");
        Language language = new Language(languageId, twoLettersCode);
        return (T) new InternationalizedCatalogEntry(id, language, value);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return null;
  }

  public T get(Language language, String value) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, language, value);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public T get(Connection connection, Language language, String value) throws ClusterException {

    String query
            = "SELECT c.id AS id "
            + "FROM " + catalogDatabaseName + "." + catalogTableName + " AS c "
            + "LEFT JOIN " + wordsDatabaseName + "." + wordsTableName + " AS w ON c.id = w.id "
            + "LEFT JOIN " + LanguagesTable.DATABASE + "." + LanguagesTable.NAME + " AS l ON w.language = l.id "
            + " WHERE language = ? AND value = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, language.getId());
      ps.setString(2, value);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        int id = rs.getInt("id");
        return (T) new InternationalizedCatalogEntry(id, language, value);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return null;
  }
}
