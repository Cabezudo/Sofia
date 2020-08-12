package net.cabezudo.sofia.countries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class CountryManager {

  private static CountryManager INSTANCE;

  public static CountryManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new CountryManager();
    }
    return INSTANCE;
  }

  public Country get(String name) throws SQLException {
    try ( Connection connection = Database.getConnection()) {
      return get(connection, name);
    }
  }

  public Country get(Connection connection, String name) throws SQLException {
    String query = "SELECT id, name, phoneCode, twoLettersCountryCode FROM " + CountriesTable.NAME + " WHERE name =  ?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setString(1, name);
      Logger.fine(ps);
      rs = ps.executeQuery();
      if (rs.next()) {
        return new Country(rs.getInt("id"), rs.getString("name"), rs.getInt("phoneCode"), rs.getString("twoLettersCountryCode"));
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

  public Country add(String name, int phoneCode, String twoLettersCountryCode) throws SQLException {
    try ( Connection connection = Database.getConnection()) {
      return add(connection, name, phoneCode, twoLettersCountryCode);
    }
  }

  public Country add(Connection connection, String name, int phoneCode, String twoLettersCountryCode) throws SQLException {
    String query = "INSERT INTO " + CountriesTable.NAME + " (name, phoneCode, twoLettersCountryCode) VALUES (?, ?, ?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, name);
      ps.setInt(2, phoneCode);
      ps.setString(3, twoLettersCountryCode);
      Logger.fine(ps);
      ps.executeUpdate();

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        Integer id = rs.getInt(1);
        return new Country(id, name, phoneCode, twoLettersCountryCode);
      }
    } finally {
      if (rs != null) {
        rs.close();
      }
      if (ps != null) {
        ps.close();
      }
    }

    throw new SQLException("Can't get the generated key");
  }
}
