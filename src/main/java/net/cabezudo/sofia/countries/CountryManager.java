package net.cabezudo.sofia.countries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.words.Word;
import net.cabezudo.sofia.core.words.WordManager;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class CountryManager {

  private static final CountryManager INSTANCE = new CountryManager();

  public static CountryManager getInstance() {
    return INSTANCE;
  }

  public Country get(Language language, String name) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, language, name);
    }
  }

  public Country get(Connection connection, Language language, String name) throws SQLException {
    String query = "SELECT id, w.id AS wordId, w.value AS wordValue, phoneCode, twoLettersCountryCode FROM " + CountriesTable.NAME + " WHERE name =  ?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setString(1, name);
      Logger.fine(ps);
      rs = ps.executeQuery();
      if (rs.next()) {
        int wordId = rs.getInt("wordId");
        String wordValue = rs.getString("wordValue");
        Word word = new Word(wordId, language, wordValue);
        return new Country(rs.getInt("id"), word, rs.getInt("phoneCode"), rs.getString("twoLettersCountryCode"));
      }
      return null;
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }
  }

  public Country add(Language language, String name, int phoneCode, String twoLettersCountryCode) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return add(connection, language, name, phoneCode, twoLettersCountryCode);
    }
  }

  public Country add(Connection connection, Language language, String name, int phoneCode, String twoLettersCountryCode) throws SQLException {

    Word word = WordManager.getInstance().get(connection, language, name);
    if (word == null) {
      word = WordManager.getInstance().add(connection, language, name);
    }

    connection.setAutoCommit(false);
    String query = "INSERT INTO " + CountriesTable.NAME + " (phoneCode, twoLettersCountryCode) VALUES (?, ?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      ps.setInt(1, phoneCode);
      ps.setString(2, twoLettersCountryCode);
      Logger.fine(ps);
      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        Integer id = rs.getInt(1);
        addCountryName(connection, id, word);
        return new Country(id, word, phoneCode, twoLettersCountryCode);
      }
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
      connection.setAutoCommit(true);
    }

    throw new SQLException("Can't get the generated key");
  }

  private void addCountryName(Connection connection, int countryId, Word word) throws SQLException {
    String query = "INSERT INTO " + CountryNamesTable.DATABASE + "." + CountryNamesTable.NAME + " (country, name) VALUES (?, ?)";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setInt(1, countryId);
      ps.setInt(2, word.getId());
      Logger.fine(ps);
      ps.executeUpdate();
    }
  }
}
