package net.cabezudo.sofia.countries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.words.Word;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class CountryManager {

  private static final CountryManager INSTANCE = new CountryManager();

  public static CountryManager getInstance() {
    return INSTANCE;
  }

  public Country get(Language language, String name) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, language, name);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Country get(Connection connection, Language language, String name) throws ClusterException {
    String query = "SELECT id, w.id AS wordId, w.value AS wordValue, phoneCode, twoLettersCountryCode FROM " + CountriesTable.NAME + " WHERE name =  ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, name);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        int wordId = rs.getInt("wordId");
        String wordValue = rs.getString("wordValue");
        Word word = new Word(wordId, language, wordValue);
        return new Country(rs.getInt("id"), rs.getString("twoLettersCountryCode"), word, rs.getInt("phoneCode"));
      }
      return null;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public Country get(String twoLettersCountryCode) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, twoLettersCountryCode);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Country get(Connection connection, String twoLettersCountryCode) throws ClusterException {
    String query = "SELECT id, phoneCode FROM " + CountriesTable.NAME + " WHERE twoLettersCountryCode =  ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setString(1, twoLettersCountryCode);
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        int id = rs.getInt("id");
        int phoneCode = rs.getInt("phoneCode");
        return new Country(id, twoLettersCountryCode, null, phoneCode);
      }
      return null;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public Country add(Language language, String name, int phoneCode, String twoLettersCountryCode) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return add(connection, language, name, phoneCode, twoLettersCountryCode);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Country add(Connection connection, Language language, String name, int phoneCode, String twoLettersCountryCode) throws ClusterException {
    try {
      connection.setAutoCommit(false);
      String query = "INSERT INTO " + CountriesTable.NAME + " (phoneCode, twoLettersCountryCode) VALUES (?, ?)";
      ResultSet rs = null;
      try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
        ps.setInt(1, phoneCode);
        ps.setString(2, twoLettersCountryCode);
        ClusterManager.getInstance().executeUpdate(ps);
        rs = ps.getGeneratedKeys();
        if (rs.next()) {
          Integer id = rs.getInt(1);
          CountryName countryName = ContryNamesManager.getInstance().get(connection, language, name);
          if (countryName == null) {
            countryName = ContryNamesManager.getInstance().add(connection, id, language, name);
          }
          addCountryName(connection, id, countryName);
          return new Country(id, twoLettersCountryCode, countryName, phoneCode);
        }
      } finally {
        ClusterManager.getInstance().close(rs);
        connection.setAutoCommit(true);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
    throw new ClusterException("Can't get the generated key");
  }

  private void addCountryName(Connection connection, int countryId, CountryName countryName) throws ClusterException {
    // The IGNORE is to allow add existent words without check if exists
    String query = "INSERT IGNORE INTO " + CountryNamesTable.DATABASE_NAME + "." + CountryNamesTable.NAME + " (id, language, value) VALUES (?, ?, ?)";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setInt(1, countryId);
      ps.setInt(2, countryName.getLanguage().getId());
      ps.setString(3, countryName.getValue());
      ClusterManager.getInstance().executeUpdate(ps);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }
}
