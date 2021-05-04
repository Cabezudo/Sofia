package net.cabezudo.sofia.core.configuration;

import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import net.cabezudo.sofia.addresses.AddressesTable;
import net.cabezudo.sofia.cities.CitiesTable;
import net.cabezudo.sofia.clients.ClientTable;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.currency.CurrenciesTable;
import net.cabezudo.sofia.core.currency.CurrencyManager;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.exceptions.DataConversionException;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.languages.InvalidTwoLettersCodeException;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.languages.LanguageManager;
import net.cabezudo.sofia.core.languages.LanguagesTable;
import net.cabezudo.sofia.core.schedule.TimeEntriesTable;
import net.cabezudo.sofia.core.schedule.TimeTypesTable;
import net.cabezudo.sofia.core.schedule.TimesTable;
import net.cabezudo.sofia.core.sites.SitesTable;
import net.cabezudo.sofia.core.sites.domainname.DomainNamesTable;
import net.cabezudo.sofia.core.users.UsersTable;
import net.cabezudo.sofia.core.users.permission.PermissionTypesTable;
import net.cabezudo.sofia.core.users.permission.PermissionsPermissionTypesTable;
import net.cabezudo.sofia.core.users.permission.PermissionsTable;
import net.cabezudo.sofia.core.users.permission.ProfilesPermissionsTable;
import net.cabezudo.sofia.core.users.profiles.ProfilesTable;
import net.cabezudo.sofia.core.users.profiles.UsersProfilesTable;
import net.cabezudo.sofia.core.webusers.WebUserDataTable;
import net.cabezudo.sofia.countries.CountriesTable;
import net.cabezudo.sofia.countries.Country;
import net.cabezudo.sofia.countries.CountryManager;
import net.cabezudo.sofia.countries.CountryNamesTable;
import net.cabezudo.sofia.emails.EMailsTable;
import net.cabezudo.sofia.geography.AdministrativeDivisionCreator;
import net.cabezudo.sofia.geography.AdministrativeDivisionNameTable;
import net.cabezudo.sofia.geography.AdministrativeDivisionTable;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.municipalities.MunicipalitiesTable;
import net.cabezudo.sofia.people.PeopleTable;
import net.cabezudo.sofia.phonenumbers.PhoneNumbersTable;
import net.cabezudo.sofia.postalcodes.MexicoPostalCodeCreator;
import net.cabezudo.sofia.postalcodes.PostalCodesTable;
import net.cabezudo.sofia.states.StatesTable;
import net.cabezudo.sofia.streets.StreetsTable;
import net.cabezudo.sofia.zones.ZonesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.26
 */
public class SofiaDatabaseCreator extends DataCreator {

  private static final String DATABASE_NAME = Configuration.getInstance().getDatabaseName();

  public SofiaDatabaseCreator() {
    try {
      Logger.info("Load the JDBC driver %s.", Configuration.getInstance().getDatabaseDriver());
      Class.forName(Configuration.getInstance().getDatabaseDriver());
    } catch (ClassNotFoundException e) {
      throw new RuntimeConfigurationException(e);
    }

    String host = Configuration.getInstance().getDatabaseHostname();
    int port = Integer.parseInt(Configuration.getInstance().getDatabasePort());

    try {
      new Socket(host, port).close();
    } catch (IOException e) {
      throw new RuntimeConfigurationException("Can't find the database server in " + host + ":" + port + ".", e);
    }
  }

  @Override
  public void dropDatabase() throws DataCreationException {
    super.dropDatabase(DATABASE_NAME);
  }

  @Override
  public boolean databaseExists() throws DataCreationException {
    return super.databaseExists(DATABASE_NAME);
  }

  @Override
  public void createDatabase() throws DataCreationException {
    super.createDatabase(DATABASE_NAME);
  }

  @Override
  public void createDatabaseStructure() throws DataCreationException {
    try (Connection connection = Database.getConnection()) {
      Database.createTable(connection, LanguagesTable.CREATION_QUERY);
      Database.createTable(connection, CurrenciesTable.CREATION_QUERY);
      Database.createTable(connection, SitesTable.CREATION_QUERY);
      Database.createTable(connection, DomainNamesTable.CREATION_QUERY);
      Database.createTable(connection, PeopleTable.CREATION_QUERY);
      Database.createTable(connection, EMailsTable.CREATION_QUERY);
      Database.createTable(connection, UsersTable.CREATION_QUERY);
      Database.createTable(connection, WebUserDataTable.CREATION_QUERY);
      Database.createTable(connection, ClientTable.CREATION_QUERY);
      Database.createTable(connection, PhoneNumbersTable.CREATION_QUERY);
      Database.createTable(connection, CountriesTable.CREATION_QUERY);
      Database.createTable(connection, CountryNamesTable.CREATION_QUERY);
      Database.createTable(connection, StatesTable.CREATION_QUERY);
      Database.createTable(connection, MunicipalitiesTable.CREATION_QUERY);
      Database.createTable(connection, CitiesTable.CREATION_QUERY);
      Database.createTable(connection, ZonesTable.CREATION_QUERY);
      Database.createTable(connection, AdministrativeDivisionTable.CREATION_QUERY);
      Database.createTable(connection, AdministrativeDivisionNameTable.CREATION_QUERY);
      Database.createTable(connection, PostalCodesTable.CREATION_QUERY);
      Database.createTable(connection, StreetsTable.CREATION_QUERY);
      Database.createTable(connection, AddressesTable.CREATION_QUERY);
      Database.createTable(connection, ProfilesTable.CREATION_QUERY);
      Database.createTable(connection, UsersProfilesTable.CREATION_QUERY);
      Database.createTable(connection, PermissionsTable.CREATION_QUERY);
      Database.createTable(connection, ProfilesPermissionsTable.CREATION_QUERY);
      Database.createTable(connection, PermissionTypesTable.CREATION_QUERY);
      Database.createTable(connection, PermissionsPermissionTypesTable.CREATION_QUERY);
      Database.createTable(connection, TimeEntriesTable.CREATION_QUERY);
      Database.createTable(connection, TimeTypesTable.CREATION_QUERY);
      Database.createTable(connection, TimesTable.CREATION_QUERY);
    } catch (SQLException | ClusterException e) {
      throw new DataCreationException(e);
    }
  }

  @Override
  public void createDefaultData() throws DataCreationException, ConfigurationException, DataConversionException {
    try {
      createCurrencies();
      createLanguages();
      createCountries();

      new AdministrativeDivisionCreator().create();

      if (Environment.getInstance().isProduction()) {
        new MexicoPostalCodeCreator().create();
      }

    } catch (ClusterException e) {
      throw new DataCreationException(e);
    }
  }

  @Override
  public void createTestData() throws DataCreationException {
    // Nothing to create for now
  }

  private void createCurrencies() throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      CurrencyManager.getInstance().add(connection, "MXN");
      CurrencyManager.getInstance().add(connection, "USD");
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  private void createLanguages() throws ClusterException {
    try (Connection connection = Database.getConnection()) {
      LanguageManager.getInstance().add(connection, "en");
      LanguageManager.getInstance().add(connection, "es");
    } catch (SQLException e) {
      throw new ClusterException(e);
    }
  }

  private Country createCountries() throws ClusterException {
    // Create countries from file
    Language language;
    try {
      language = LanguageManager.getInstance().get("es");
    } catch (InvalidTwoLettersCodeException e) {
      throw new SofiaRuntimeException(e);
    }
    return CountryManager.getInstance().add(language, "México", 52, "MX");
  }
}
