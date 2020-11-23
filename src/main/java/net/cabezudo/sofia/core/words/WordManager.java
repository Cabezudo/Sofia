package net.cabezudo.sofia.core.words;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.languages.LanguagesTable;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.11.23
 */
public class WordManager {

  private static final WordManager INSTANCE = new WordManager();

  private WordManager() {
    // Nothing to do here. Jus protect the instance
  }

  public static WordManager getInstance() {
    return INSTANCE;
  }

  public Word get(Connection connection, Language language, String value) throws SQLException {
    String query
            = "SELECT w.id AS wordId, l.id AS languageId, twoLettersCode, value "
            + "FROM " + WordsTable.DATABASE + "." + WordsTable.NAME + " AS w "
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
        return new Word(wordId, new Language(languageId, twoLettersCode), wordValue);
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

  public Word add(Connection connection, Language language, String value) throws SQLException {
    String query = "INSERT INTO " + WordsTable.DATABASE + "." + WordsTable.NAME + " (language, value) VALUES (?, ?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      ps.setInt(1, language.getId());
      ps.setString(2, value);

      Logger.fine(ps);
      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new Word(id, language, value);
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
}
