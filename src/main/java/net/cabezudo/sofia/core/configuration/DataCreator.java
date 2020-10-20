package net.cabezudo.sofia.core.configuration;

import java.sql.SQLException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.10.10
 */
public abstract class DataCreator {

  private boolean databaseCreated = false;
  private final JSONObject jsonAPIConfiguration = new JSONObject();

  public abstract void dropDatabase() throws DataCreationException;

  protected void dropDatabase(String databaseName) throws DataCreationException {
    Logger.info("Drop `" + databaseName + "` database on demand from the command line.");
    try {
      Database.drop(databaseName);
    } catch (SQLException e) {
      throw new DataCreationException(e);
    }
  }

  public abstract boolean databaseExists() throws DataCreationException;

  protected boolean databaseExists(String databaseName) throws DataCreationException {
    try {
      return Database.exist(databaseName);
    } catch (SQLException e) {
      throw new DataCreationException(e);
    }
  }

  public abstract void createDatabase() throws DataCreationException;

  protected void createDatabase(String databaseName) throws DataCreationException {
    try {
      Database.createDatabase(databaseName);
    } catch (SQLException e) {
      throw new DataCreationException(e);
    }
  }

  public abstract void createDatabaseStructure() throws DataCreationException;

  void createDatabaseStructure(String databaseName) throws DataCreationException {
    try {
      Database.createDatabase(databaseName);
    } catch (SQLException e) {
      throw new DataCreationException(e);
    }
  }

  public void riseDatabaseCreatedFlag() {
    databaseCreated = true;
  }

  public boolean isDatabaseCreated() {
    return databaseCreated;
  }

  public abstract void createDefaultData() throws DataCreationException;

  public abstract void createTestData() throws DataCreationException;

  public void addAPIConfiguration(JSONObject jsonObject) {
    jsonAPIConfiguration.merge(jsonObject);
  }

  public JSONObject getAPIConfiguration() {
    return jsonAPIConfiguration;
  }
}
