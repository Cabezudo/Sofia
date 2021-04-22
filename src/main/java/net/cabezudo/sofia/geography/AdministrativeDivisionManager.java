package net.cabezudo.sofia.geography;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.ElementNotExistException;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.cluster.ClusterManager;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.geolocation.Latitude;
import net.cabezudo.sofia.core.geolocation.Longitude;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.geometry.Point;
import net.cabezudo.sofia.geometry.Polygon;
import net.cabezudo.sofia.geometry.Polygons;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.20
 */
public class AdministrativeDivisionManager {

  public static final String ADMINISTRATIVE_DIVISION_DATA_PATH_NAME = "AdministrativeDivisions";
  private static AdministrativeDivisionManager INSTANCE;

  private final Path dataPath;

  public static AdministrativeDivisionManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new AdministrativeDivisionManager();
    }
    return INSTANCE;
  }

  private AdministrativeDivisionManager() {
    Path systemDataPath = Configuration.getInstance().getSystemDataPath();
    dataPath = systemDataPath.resolve(ADMINISTRATIVE_DIVISION_DATA_PATH_NAME);
  }

  public AdministrativeDivision get(Longitude longitude, Latitude latitude, AdministrativeDivisionType administrativeDivisionType) throws JSONParseException, IOException, ClusterException, InvalidPolygonDataException {
    try {
      return get(null, longitude, latitude, administrativeDivisionType);
    } catch (PropertyNotExistException e) {
      throw new SofiaRuntimeException(e);
    }
  }

  private List<AdministrativeDivision> getChilds(AdministrativeDivision parent) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return getChils(connection, parent);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public List<AdministrativeDivision> getChils(Connection connection, AdministrativeDivision parent) throws ClusterException {
    Logger.fine("Get childs for " + parent);

    String query
            = "SELECT id, type, code, fileId "
            + "FROM " + AdministrativeDivisionTable.DATABASE_NAME + "." + AdministrativeDivisionTable.NAME + " AS ad "
            + "WHERE parent = ?";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, parent == null ? 0 : parent.getId());
      rs = ClusterManager.getInstance().executeQuery(ps);
      ArrayList<AdministrativeDivision> list = new ArrayList<>();
      while (rs.next()) {
        int id = rs.getInt("id");
        int typeId = rs.getInt("type");
        String code = rs.getString("code");
        int fileId = rs.getInt("fileId");
        AdministrativeDivisionType type = AdministrativeDivisionTypeManager.getInstance().get(typeId);
        list.add(new AdministrativeDivision(id, type, code, fileId, parent));
      }
      return list;
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }

  }

  private AdministrativeDivision get(AdministrativeDivision parent, Longitude longitude, Latitude latitude, AdministrativeDivisionType administrativeDivisionType)
          throws JSONParseException, IOException, PropertyNotExistException, ClusterException, InvalidPolygonDataException {

    Point point = new Point(longitude.toDouble(), latitude.toDouble());

    List<AdministrativeDivision> list = getChilds(parent);

    for (AdministrativeDivision administrativeDivision : list) {
      int fileId = administrativeDivision.getFileId();
      if (fileId == 0) {
        AdministrativeDivision actualAdministrativeDivision = get(administrativeDivision, longitude, latitude, administrativeDivisionType);
        if (actualAdministrativeDivision != null) {
          return actualAdministrativeDivision;
        }
      } else {
        // TODO add a cache for the object generated
        Path administrativeDivisionDataFilePath = dataPath.resolve(fileId + ".json");
        JSONValue jsonAdministrativeDivisionData = JSON.parse(administrativeDivisionDataFilePath, Configuration.getDefaultCharset());
        JSONArray jsonjsonAdministrativeDivisionDataDataArray = jsonAdministrativeDivisionData.toJSONArray();
        Polygons polygons = createPolygon(jsonjsonAdministrativeDivisionDataDataArray);

        if (polygons.isInside(point)) {
          if (administrativeDivision.getType().equals(administrativeDivisionType)) {
            return administrativeDivision;
          }
          AdministrativeDivision result = get(administrativeDivision, longitude, latitude, administrativeDivisionType);
          if (result != null) {
            return result;
          }
          return administrativeDivision;
        }
      }
    }
    return null;
  }

  private Polygons createPolygon(JSONArray data) throws InvalidPolygonDataException {
    Polygons polygons = new Polygons();
    for (JSONValue jsonValue : data) {
      JSONArray polygonData = jsonValue.toJSONArray();
      Polygon polygon = new Polygon();
      for (int i = 0; i < polygonData.size(); i += 2) {
        try {
          double longitude = polygonData.getDouble(i);
          double latitude = polygonData.getDouble(i + 1);
          Point point = new Point(longitude, latitude);
          polygon.add(point);
        } catch (ElementNotExistException e) {
          throw new InvalidPolygonDataException(e);
        }
      }
      polygons.add(polygon);
    }
    return polygons;
  }

  public AdministrativeDivision add(AdministrativeDivisionType administrativeDivisionType, String code, Integer fileId, AdministrativeDivision parent) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      return add(connection, administrativeDivisionType, code, fileId, parent);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public AdministrativeDivision add(Connection connection, AdministrativeDivisionType type, String code, Integer fileId, AdministrativeDivision parent) throws ClusterException {
    String query = "INSERT INTO " + AdministrativeDivisionTable.DATABASE_NAME + "." + AdministrativeDivisionTable.NAME + " (`type`, `code`, `fileId`, `parent`) VALUES (?, ?, ?, ?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
      ps.setInt(1, type.getId());
      ps.setString(2, code);
      ps.setInt(3, fileId == null ? 0 : fileId);
      ps.setInt(4, parent == null ? 0 : parent.getId());
      ClusterManager.getInstance().executeUpdate(ps);
      rs = ps.getGeneratedKeys();
      if (rs.next()) {
        int id = rs.getInt(1);
        return new AdministrativeDivision(id, type, code, fileId, parent);
      }
      throw new SofiaRuntimeException("Can't get the generated key");
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }

  public void add(AdministrativeDivision administrativeDivision, Language language, String name) throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      add(connection, administrativeDivision, language, name);
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  public AdministrativeDivisionName add(Connection connection, AdministrativeDivision administrativeDivision, Language language, String name) throws ClusterException {
    int id = administrativeDivision.getId();

    String query = "INSERT INTO " + AdministrativeDivisionNameTable.DATABASE_NAME + "." + AdministrativeDivisionNameTable.NAME + " (`id`, `language`, `value`) VALUES (?, ?, ?)";
    ResultSet rs = null;
    try (PreparedStatement ps = connection.prepareStatement(query);) {
      ps.setInt(1, id);
      ps.setInt(2, language.getId());
      ps.setString(3, name);
      ClusterManager.getInstance().executeUpdate(ps);
      return new AdministrativeDivisionName(id, language, name);
    } catch (SQLException e) {
      throw new ClusterException(e);
    } finally {
      ClusterManager.getInstance().close(rs);
    }
  }
}
