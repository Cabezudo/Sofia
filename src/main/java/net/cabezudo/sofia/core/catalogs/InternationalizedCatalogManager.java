package net.cabezudo.sofia.core.catalogs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.languages.LanguagesTable;
import net.cabezudo.sofia.logger.Logger;

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

  public T add(Language language, String value) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return add(connection, language, value);
    }
  }

  public T add(Connection connection, Language language, String value) throws SQLException {
    String query = "INSERT INTO " + catalogDatabaseName + "." + catalogTableName + " () VALUES ()";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

      Logger.fine(ps);
      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return (T) addWordEntry(connection, id, language, value);
      }
      throw new SofiaRuntimeException("Can't get the generated key");
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
  }

  public InternationalizedCatalogEntry add(Connection connection, InternationalizedCatalogEntry entry, Language language, String value) throws SQLException {
    return addWordEntry(connection, entry.getId(), language, value);
  }

  private InternationalizedCatalogEntry addWordEntry(Connection connection, int targetId, Language language, String value) throws SQLException {
    String query = "INSERT INTO " + wordsDatabaseName + "." + wordsTableName + " (id, language, value) VALUES (?, ?, ?)";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setInt(1, targetId);
      ps.setInt(2, language.getId());
      ps.setString(3, value);
      Logger.fine(ps);
      ps.executeUpdate();
      return new InternationalizedCatalogEntry(targetId, language, value);
    }
  }

  public T get(int id) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, id);
    }
  }

  public T get(Connection connection, int id) throws SQLException {
    String query
            = "SELECT w.value AS value, l.id AS languageId, l.twoLettersCode AS twoLettersCode "
            + "FROM " + catalogDatabaseName + "." + catalogTableName + " AS c "
            + "LEFT JOIN " + wordsDatabaseName + "." + wordsTableName + " AS w ON c.id = w.id "
            + "LEFT JOIN " + LanguagesTable.DATABASE + "." + LanguagesTable.NAME + " AS l ON w.language = l.id "
            + "WHERE id = ?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setInt(1, id);
      Logger.fine(ps);
      rs = ps.executeQuery();
      if (rs.next()) {
        int languageId = rs.getInt("languageId");
        String twoLettersCode = rs.getString("twoLettersCode");
        String value = rs.getString("value");
        Language language = new Language(languageId, twoLettersCode);
        return (T) new InternationalizedCatalogEntry(id, language, value);
      }
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
    return null;
  }

  public T get(Language language, String value) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, language, value);
    }
  }

  public T get(Connection connection, Language language, String value) throws SQLException {

    String query
            = "SELECT c.id AS id "
            + "FROM " + catalogDatabaseName + "." + catalogTableName + " AS c "
            + "LEFT JOIN " + wordsDatabaseName + "." + wordsTableName + " AS w ON c.id = w.id "
            + "LEFT JOIN " + LanguagesTable.DATABASE + "." + LanguagesTable.NAME + " AS l ON w.language = l.id "
            + " WHERE language = ? AND value = ?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setInt(1, language.getId());
      ps.setString(2, value);
      Logger.fine(ps);
      rs = ps.executeQuery();
      if (rs.next()) {
        int id = rs.getInt("id");
        return (T) new InternationalizedCatalogEntry(id, language, value);
      }
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
    return null;
  }
}
