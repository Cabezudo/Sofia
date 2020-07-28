package net.cabezudo.sofia.settlements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.cities.CitiesTable;
import net.cabezudo.sofia.cities.City;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.countries.CountriesTable;
import net.cabezudo.sofia.countries.Country;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.municipalities.MunicipalitiesTable;
import net.cabezudo.sofia.municipalities.Municipality;
import net.cabezudo.sofia.states.State;
import net.cabezudo.sofia.states.StatesTable;
import net.cabezudo.sofia.zones.Zone;
import net.cabezudo.sofia.zones.ZonesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class SettlementManager {

  private static SettlementManager INSTANCE;

  public static SettlementManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new SettlementManager();
    }
    return INSTANCE;
  }

  public Settlement get(SettlementType settlementType, Municipality municipality, Zone zone, String name, User owner) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, settlementType, municipality, zone, name, owner);
    }
  }

  public Settlement get(Connection connection, SettlementType settlementType, Municipality municipality, Zone zone, String name, User owner) throws SQLException {
    String query = "SELECT "
            + "s.id AS id, "
            + "st.id AS typeId, "
            + "st.name AS typeName, "
            + "c.id AS cityId, "
            + "c.name AS cityName, "
            + "m.id AS municipalityId, "
            + "m.name AS municipalityName, "
            + "t.id AS stateId, "
            + "t.name AS stateName, "
            + "o.id AS countryId, "
            + "o.name AS countryName, "
            + "o.phoneCode AS countryPhoneCode, "
            + "o.twoLettersCountryCode AS countryTwoLettersCountryCode, "
            + "z.id AS zoneId, "
            + "z.name AS zoneName, "
            + "s.name AS name "
            + "FROM " + SettlementsTable.NAME + " AS s "
            + "LEFT JOIN " + SettlementTypesTable.NAME + " AS st ON s.type = st.id "
            + "LEFT JOIN " + CitiesTable.NAME + " AS c ON s.city = c.id "
            + "LEFT JOIN " + MunicipalitiesTable.NAME + " AS m ON s.municipality = m.id "
            + "LEFT JOIN " + StatesTable.NAME + " AS t ON m.state = t.id "
            + "LEFT JOIN " + CountriesTable.NAME + " AS o ON t.country = o.id "
            + "LEFT JOIN " + ZonesTable.NAME + " AS z ON s.zone = z.id "
            + "WHERE st.id = ? AND m.id = ? AND z.id = ? AND s.name = ? AND (s.owner = ? OR s.owner = 1)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = connection.prepareStatement(query);
      ps.setInt(1, settlementType.getId());
      ps.setInt(2, municipality.getId());
      ps.setInt(3, zone.getId());
      ps.setString(4, name);
      ps.setInt(5, owner.getId());
      Logger.fine(ps);
      rs = ps.executeQuery();

      if (rs.next()) {
        settlementType = new SettlementType(rs.getInt("typeId"), rs.getString("typeName"));
        Country country = new Country(rs.getInt("countryId"), rs.getString("countryName"), rs.getInt("countryPhoneCode"), rs.getString("countryTwoLettersCountryCode"));
        State state = new State(rs.getInt("stateId"), country, rs.getString("stateName"));
        City city = null;
        if (rs.getInt("cityId") != 0) {
          city = new City(rs.getInt("cityId"), state, rs.getString("cityName"));
        }
        municipality = new Municipality(rs.getInt("municipalityId"), state, rs.getString("municipalityName"));
        zone = new Zone(rs.getInt("zoneId"), rs.getString("zoneName"));
        return new Settlement(rs.getInt("id"), settlementType, city, municipality, zone, rs.getString("name"));
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

  public Settlement add(SettlementType settlementType, City city, Municipality municipality, Zone zone, String settlementName, User owner) throws SQLException {
    try (Connection connection = Database.getConnection()) {
      return add(connection, settlementType, city, municipality, zone, settlementName, owner);
    }
  }

  public Settlement add(Connection connection, SettlementType settlementType, City city, Municipality municipality, Zone zone, String name, User owner) throws SQLException {
    Settlement settlement = get(connection, settlementType, municipality, zone, name, owner);
    if (settlement != null) {
      return settlement;
    }

    String query = "INSERT INTO " + SettlementsTable.NAME + " (type, city, municipality, zone, name, owner) VALUES (?, ?, ?, ?, ?, ?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setInt(1, settlementType.getId());
      if (city == null) {
        ps.setInt(2, 0);
      } else {
        ps.setInt(2, city.getId());
      }
      ps.setInt(3, municipality.getId());
      ps.setInt(4, zone.getId());
      ps.setString(5, name);
      ps.setInt(6, owner.getId());
      Logger.fine(ps);
      ps.executeUpdate();
      connection.setAutoCommit(true);

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new Settlement(id, settlementType, city, municipality, zone, name);
      }
    } finally {
      if (rs != null) {
        rs.close();
      }
    }
    throw new SofiaRuntimeException("Can't get the generated key");
  }

}