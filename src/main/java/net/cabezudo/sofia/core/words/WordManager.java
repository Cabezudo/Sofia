package net.cabezudo.sofia.core.words;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.languages.LanguagesTable;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.11.23
 * @param <T>
 */
public abstract class WordManager<T extends Word> {

  private final String databaseName;
  private final String tableName;

  protected WordManager(String databaseName, String tableName) {
    this.databaseName = databaseName;
    this.tableName = tableName;
  }

  public T get(Connection connection, Language language, String value) throws SQLException {
    String query
            = "SELECT w.id AS wordId, l.id AS languageId, twoLettersCode, value "
            + "FROM " + databaseName + "." + tableName + " AS w "
            + "LEFT JOIN " + LanguagesTable.DATABASE + "." + LanguagesTable.NAME + " AS l ON w.language = l.id "
            + "WHERE l.id = ? AND w.value = ?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setInt(1, language.getId());
      ps.setString(2, value);
      Logger.fine(ps);
      rs = ps.executeQuery();
      if (rs.next()) {
        int wordId = rs.getInt("wordId");
        String wordValue = rs.getString("value");
        int languageId = rs.getInt("languageId");
        String twoLettersCode = rs.getString("twoLettersCode");
        return (T) new Word(wordId, new Language(languageId, twoLettersCode), wordValue);
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

  public T add(Connection connection, int targetId, Language language, String value) throws SQLException {
    String query = "INSERT INTO " + databaseName + "." + tableName + " (id, language, value) VALUES (?, ?, ?)";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setInt(1, targetId);
      ps.setInt(2, language.getId());
      ps.setString(3, value);

      Logger.fine(ps);
      ps.executeUpdate();
      return (T) new Word(targetId, language, value);
    }
  }
}
