package net.cabezudo.sofia.settlements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.cabezudo.sofia.cities.CitiesTable;
import net.cabezudo.sofia.cities.City;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.geolocation.Latitude;
import net.cabezudo.sofia.core.geolocation.Longitude;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.words.Word;
import net.cabezudo.sofia.countries.CountriesTable;
import net.cabezudo.sofia.countries.Country;
import net.cabezudo.sofia.countries.CountryNamesTable;
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

  public Settlement get(Language language, SettlementType settlementType, Municipality municipality, Zone zone, String municipalityId, String name, User owner) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return get(connection, language, settlementType, municipality, zone, municipalityId, name, owner);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Settlement get(Connection connection, Language language, SettlementType settlementType, Municipality municipality, Zone zone, String municipalityId, String name, User owner) throws ClusterException {
    String query = "SELECT "
            + "s.id AS id, "
            + "st.id AS typeId, "
            + "st.name AS typeName, "
            + "c.id AS cityId, "
            + "c.name AS cityName, "
            + "c.latitude AS latitude, "
            + "c.longitude AS longitude, "
            + "m.id AS municipalityId, "
            + "m.name AS municipalityName, "
            + "t.id AS stateId, "
            + "t.name AS stateName, "
            + "o.id AS countryId, "
            + "o.phoneCode AS countryPhoneCode, "
            + "o.twoLettersCountryCode AS countryTwoLettersCountryCode, "
            + "cn.id AS countryNameId, "
            + "cn.value AS countryName, "
            + "z.id AS zoneId, "
            + "z.name AS zoneName, "
            + "s.name AS name "
            + "FROM " + SettlementsTable.NAME + " AS s "
            + "LEFT JOIN " + SettlementTypesTable.NAME + " AS st ON s.type = st.id "
            + "LEFT JOIN " + CitiesTable.NAME + " AS c ON s.city = c.id "
            + "LEFT JOIN " + MunicipalitiesTable.NAME + " AS m ON s.municipality = m.id "
            + "LEFT JOIN " + StatesTable.NAME + " AS t ON m.state = t.id "
            + "LEFT JOIN " + CountriesTable.NAME + " AS o ON t.country = o.id "
            + "LEFT JOIN " + CountryNamesTable.NAME + " AS cn ON c.id = cn.id "
            + "LEFT JOIN " + ZonesTable.NAME + " AS z ON s.zone = z.id "
            + "WHERE cn.language = ? AND st.id = ? AND m.id = ? AND z.id = ? AND s.name = ? AND (s.owner = ? OR s.owner = 1)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, language.getId());
      ps.setInt(2, settlementType.getId());
      ps.setInt(3, municipality.getId());
      ps.setInt(4, zone.getId());
      ps.setString(5, name);
      ps.setInt(6, owner.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);
      if (rs.next()) {
        settlementType = new SettlementType(rs.getInt("typeId"), rs.getString("typeName"));
        int countryNameId = rs.getInt("countryNameId");
        String countryNameString = rs.getString("countryName");
        Word countryName = new Word(countryNameId, language, countryNameString);
        Country country = new Country(rs.getInt("countryId"), rs.getString("countryTwoLettersCountryCode"), countryName, rs.getInt("countryPhoneCode"));
        State state = new State(rs.getInt("stateId"), country, rs.getString("stateName"));
        City city = null;
        if (rs.getInt("cityId") != 0) {
          city = new City(rs.getInt("cityId"), state, rs.getString("cityName"), new Latitude(rs.getBigDecimal("latitude")), new Longitude(rs.getBigDecimal("longitude")));
        }
        municipality = new Municipality(rs.getInt("municipalityId"), state, rs.getString("municipalityName"));
        zone = new Zone(rs.getInt("zoneId"), rs.getString("zoneName"));
        return new Settlement(rs.getInt("id"), settlementType, city, municipality, zone, rs.getString("name"));
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    return null;
  }

  public Settlement add(Language language, SettlementType settlementType, City city, Municipality municipality, Zone zone, String municipalitySettlementId, String settlementName, User owner) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return add(connection, language, settlementType, city, municipality, zone, municipalitySettlementId, settlementName, owner);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public Settlement add(Connection connection, Language language, SettlementType settlementType, City city, Municipality municipality, Zone zone, String municipalityId, String name, User owner) throws ClusterException {
    Settlement settlement = get(connection, language, settlementType, municipality, zone, municipalityId, name, owner);
    if (settlement != null) {
      return settlement;
    }

    String query = "INSERT INTO " + SettlementsTable.NAME + " (type, city, municipality, zone, municipalityId, name, owner) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
      ps.setString(5, municipalityId);
      ps.setString(6, name);
      ps.setInt(7, owner.getId());

      ClusterManager.getInstance().executeUpdate(ps);

      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new Settlement(id, settlementType, city, municipality, zone, name);
      }
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
    throw new SofiaRuntimeException("Can't get the generated key");
  }

}
