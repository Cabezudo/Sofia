package net.cabezudo.sofia.core.configuration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Currency;
import java.util.regex.Pattern;
import net.cabezudo.sofia.addresses.AddressesTable;
import net.cabezudo.sofia.cities.CitiesTable;
import net.cabezudo.sofia.cities.City;
import net.cabezudo.sofia.cities.CityManager;
import net.cabezudo.sofia.clients.ClientTable;
import net.cabezudo.sofia.core.StartOptions;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.money.Money;
import net.cabezudo.sofia.core.server.html.URLManager;
import net.cabezudo.sofia.core.server.html.URLTable;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.sites.SitesTable;
import net.cabezudo.sofia.core.sites.domainname.DomainNameManager;
import net.cabezudo.sofia.core.sites.domainname.DomainNameMaxSizeException;
import net.cabezudo.sofia.core.sites.domainname.DomainNameNotExistsException;
import net.cabezudo.sofia.core.sites.domainname.DomainNamesTable;
import net.cabezudo.sofia.core.sites.domainname.EmptyDomainNameException;
import net.cabezudo.sofia.core.sites.domainname.InvalidCharacterException;
import net.cabezudo.sofia.core.sites.domainname.MissingDotException;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.core.users.UserNotExistException;
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
import net.cabezudo.sofia.emails.EMailNotExistException;
import net.cabezudo.sofia.emails.EMailsTable;
import net.cabezudo.sofia.languages.LanguagesTable;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.municipalities.MunicipalitiesTable;
import net.cabezudo.sofia.municipalities.Municipality;
import net.cabezudo.sofia.municipalities.MunicipalityManager;
import net.cabezudo.sofia.people.PeopleTable;
import net.cabezudo.sofia.phonenumbers.PhoneNumbersTable;
import net.cabezudo.sofia.postalcodes.PostalCodeManager;
import net.cabezudo.sofia.postalcodes.PostalCodesTable;
import net.cabezudo.sofia.restaurants.Restaurant;
import net.cabezudo.sofia.restaurants.RestaurantManager;
import net.cabezudo.sofia.restaurants.RestaurantType;
import net.cabezudo.sofia.restaurants.RestaurantTypeManager;
import net.cabezudo.sofia.restaurants.RestaurantTypesTable;
import net.cabezudo.sofia.restaurants.RestaurantsTable;
import net.cabezudo.sofia.settlements.Settlement;
import net.cabezudo.sofia.settlements.SettlementManager;
import net.cabezudo.sofia.settlements.SettlementType;
import net.cabezudo.sofia.settlements.SettlementTypeManager;
import net.cabezudo.sofia.settlements.SettlementTypesTable;
import net.cabezudo.sofia.settlements.SettlementsTable;
import net.cabezudo.sofia.states.State;
import net.cabezudo.sofia.states.StateManager;
import net.cabezudo.sofia.states.StatesTable;
import net.cabezudo.sofia.streets.StreetsTable;
import net.cabezudo.sofia.zones.Zone;
import net.cabezudo.sofia.zones.ZoneManager;
import net.cabezudo.sofia.zones.ZonesTable;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.26
 */
public class DefaultData {

  private static void createSofiaDatabaseStructure() throws SQLException {
    Database.createDatabase("sofia");
    try ( Connection connection = Database.getConnection()) {
      Database.createTable(connection, SitesTable.CREATION_QUERY);
      Database.createTable(connection, URLTable.CREATION_QUERY);
      Database.createTable(connection, DomainNamesTable.CREATION_QUERY);
      Database.createTable(connection, PeopleTable.CREATION_QUERY);
      Database.createTable(connection, EMailsTable.CREATION_QUERY);
      Database.createTable(connection, UsersTable.CREATION_QUERY);
      Database.createTable(connection, WebUserDataTable.CREATION_QUERY);
      Database.createTable(connection, ClientTable.CREATION_QUERY);
      Database.createTable(connection, LanguagesTable.CREATION_QUERY);
      Database.createTable(connection, PhoneNumbersTable.CREATION_QUERY);
      Database.createTable(connection, CountriesTable.CREATION_QUERY);
      Database.createTable(connection, StatesTable.CREATION_QUERY);
      Database.createTable(connection, MunicipalitiesTable.CREATION_QUERY);
      Database.createTable(connection, CitiesTable.CREATION_QUERY);
      Database.createTable(connection, SettlementTypesTable.CREATION_QUERY);
      Database.createTable(connection, ZonesTable.CREATION_QUERY);
      Database.createTable(connection, SettlementsTable.CREATION_QUERY);
      Database.createTable(connection, PostalCodesTable.CREATION_QUERY);
      Database.createTable(connection, StreetsTable.CREATION_QUERY);
      Database.createTable(connection, AddressesTable.CREATION_QUERY);
      Database.createTable(connection, ProfilesTable.CREATION_QUERY);
      Database.createTable(connection, UsersProfilesTable.CREATION_QUERY);
      Database.createTable(connection, PermissionsTable.CREATION_QUERY);
      Database.createTable(connection, ProfilesPermissionsTable.CREATION_QUERY);
      Database.createTable(connection, PermissionTypesTable.CREATION_QUERY);
      Database.createTable(connection, PermissionsPermissionTypesTable.CREATION_QUERY);
    }
  }

  private static void createHayQueComerDatabase() throws SQLException {
    Database.createDatabase("hayQueComer");
    try ( Connection connection = Database.getConnection("hayQueComer")) {
      Database.createTable(connection, RestaurantTypesTable.CREATION_QUERY);
      Database.createTable(connection, RestaurantsTable.CREATION_QUERY);
    }
  }

  private static void createNutricionDigital() throws SQLException {
    Database.createDatabase("nutricionDigital");
//    try ( Connection connection = Database.getConnection()) {
//      Database.createTable(connection, RestaurantTypesTable.CREATION_QUERY);
//    }
  }

  private DefaultData() {
    // Nothing to do here. Utility classes should not have public constructors.
  }

  public static void create(StartOptions startOptions) throws EMailNotExistException, FileNotFoundException, UserNotExistException {
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
      throw new RuntimeConfigurationException("Can't find the database in " + host + ":" + port + ".", e);
    }

    try {
      if (startOptions.hasDropDatabase()) {
        Logger.info("Drop database on demand from the command line.");
        Database.drop();
      }
      if (!Database.exist(Configuration.getInstance().getDatabaseName())) {
        createSofiaDatabaseStructure();
        createSites();
        UserManager.getInstance().createAdministrator();
        createData();
      }
    } catch (SQLException | IOException e) {
      throw new RuntimeConfigurationException("Error in system configuration listener.", e);
    }
  }

  private static void createData() throws SQLException {
    Country country = createCountries();
    createPostalCodes(country, null);
    createRestaurants();
  }

  private static void createSites() throws SQLException, IOException {
    Logger.info("Create sites.");
    if (System.console() != null) {
      askUser();
    } else {
      SiteManager.getInstance().create("Manager", "manager", "localhost");
    }

    SiteManager.getInstance().create("Playground", "playground");
    SiteManager.getInstance().create("Menú electrónico", "hayquecomer.com", "cdmx.menu");
    createHayQueComerDatabase();

    SiteManager.getInstance().create("Nutrición digital", "nutricion.digital");
    createNutricionDigital();
  }

  private static Site askUser() throws SQLException, IOException {
    String baseDomainName;
    Utils.consoleOutLn("Crear sitio para el servidor.");
    Utils.consoleOutLn(
            "Coloque el nombre del host con el cual desea administrar el sitio en la red. Debe ser un nombre de dominio válido. "
            + "Si solo va a trabajar de forma local puede dejarlo en blanco y utilizar localhost para acceder a la configuración. "
            + "Pero si necesita acceder al sitio de forma remota debera colocar un nombre de dominio válido");
    Utils.consoleOut("Dominio base: ");
    boolean validDomain = false;
    do {
      baseDomainName = System.console().readLine();
      if (baseDomainName.isEmpty()) {
        break;
      }
      try {
        DomainNameManager.getInstance().validate(baseDomainName);
        validDomain = true;
      } catch (EmptyDomainNameException e) {
        Utils.consoleOutLn("The domain name is empty.");
      } catch (InvalidCharacterException e) {
        Utils.consoleOutLn("Invalid character '" + e.getChar() + "' in domain name");
      } catch (DomainNameNotExistsException e) {
        Utils.consoleOutLn("The domain name doesn't exist. Don't hava a DNS entry.");
      } catch (DomainNameMaxSizeException e) {
        Utils.consoleOutLn("The domain name is too large.");
      } catch (MissingDotException e) {
        Utils.consoleOutLn("A domain name must have a dot in it.");
      }
    } while (!validDomain);
    return SiteManager.getInstance().create("Manager", "manager", "localhost", baseDomainName);
  }

  private static Country createCountries() throws SQLException {
    return CountryManager.getInstance().add("México", 52, "MX");
  }

  private static void createPostalCodes(Country country, User owner) throws SQLException {
    Logger.info("Create postal codes.");
    Path postalCodesPath = Configuration.getInstance().getSystemDataPath().resolve("postalCodes.csv");
    if (Files.exists(postalCodesPath)) {
      try ( BufferedReader br = new BufferedReader(new FileReader(postalCodesPath.toFile()))) {
        String line;
        boolean headers = true;
        int counter = 0;
        while ((line = br.readLine()) != null) {
          if (!Environment.getInstance().isProduction()) {
            if (counter < 1000) {
              counter++;
              continue;
            } else {
              counter = 0;
            }
          }
          if (headers) {
            headers = false;
            continue;
          }
          String[] fields = line.split(Pattern.quote("|"));

          String stateName = fields[0];
          String cityName = fields[1];
          String municipalityName = fields[2];
          String settlementName = fields[3];
          String settlementTypeName = fields[4];
          String zoneName = fields[5];
          String postalCodeNumber = fields[6];

          State state = StateManager.getInstance().add(country, stateName);

          City city = null;
          if (!cityName.equals("-")) {
            city = CityManager.getInstance().add(state, cityName, owner);
          }

          try ( Connection connection = Database.getConnection()) {
            SettlementType settlementType = SettlementTypeManager.getInstance().add(connection, settlementTypeName);
            Municipality municipality = MunicipalityManager.getInstance().add(connection, state, municipalityName, owner);
            Zone zone = ZoneManager.getInstance().add(connection, zoneName);
            Settlement settlement = SettlementManager.getInstance().add(connection, settlementType, city, municipality, zone, settlementName, owner);
            PostalCodeManager.getInstance().add(connection, settlement, Integer.parseInt(postalCodeNumber), owner);
          }
        }
      } catch (IOException e) {
        throw new RuntimeConfigurationException(e);
      }
    }
  }

  private static void createRestaurants() throws SQLException {
    Logger.debug("Create restaurante data");

    URLManager urlManager = URLManager.getInstance();
    try ( Connection connection = Database.getConnection();) {
      Site site = SiteManager.getInstance().getByHostame(connection, "hayquecomer.com");

      RestaurantTypeManager restaurantTypeManager = RestaurantTypeManager.getInstance();

      restaurantTypeManager.add(connection, "Argentina");
      restaurantTypeManager.add(connection, "Postres");
      restaurantTypeManager.add(connection, "Americana");

      RestaurantType type;

      RestaurantManager restaurantManager = RestaurantManager.getInstance();
      Restaurant restaurant;

      type = RestaurantTypeManager.getInstance().get("Argentina");
      restaurant = new Restaurant("donbeto", "donbeto.01.jpg", "Parrillada Don Beto", type, Currency.getInstance("MXN"));
      restaurant.setPriceRange(2);
      restaurant.setShippingCost(new Money(20, Currency.getInstance("MXN")));
      restaurant.setDeliveryTime(30);
      restaurantManager.add(connection, restaurant);
      urlManager.add(connection, site, "donbeto.cdmx.menu", "donbeto");

      restaurant = new Restaurant("bariloche", "bariloche.01.jpg", "Bariloche", type, Currency.getInstance("MXN"));
      restaurant.setPriceRange(2);
      restaurant.setShippingCost(new Money(30, Currency.getInstance("MXN")));
      restaurant.setDeliveryTime(35);
      restaurantManager.add(connection, restaurant);
      urlManager.add(connection, site, "bariloche.cdmx.menu", "bariloche");

      restaurant = new Restaurant("casquet", "casquet.01.jpg", "Casquet", type, Currency.getInstance("MXN"));
      restaurant.setPriceRange(2);
      restaurant.setShippingCost(new Money(20, Currency.getInstance("MXN")));
      restaurant.setDeliveryTime(30);
      restaurantManager.add(connection, restaurant);
      urlManager.add(connection, site, "casquet.cdmx.menu", "casquet");

      type = RestaurantTypeManager.getInstance().get("Postres");
      restaurant = new Restaurant("heladosdolphy", "heladosdolphy.01.jpg", "Helados Dolphy", type, Currency.getInstance("MXN"));
      restaurant.setLocation("Dakota");
      restaurant.setPriceRange(1);
      restaurant.setShippingCost(new Money(18, Currency.getInstance("MXN")));
      restaurant.setMinDeliveryTime(20);
      restaurant.setMaxDeliveryTime(30);
      restaurantManager.add(connection, restaurant);
      urlManager.add(connection, site, "heladosdolphy.cdmx.menu", "heladosdolphy");

      type = RestaurantTypeManager.getInstance().get("Americana");
      restaurant = new Restaurant("pinchegringobbq", "pinchegringobbq.01.jpg", "Pinche Gringo BBQ", type, Currency.getInstance("MXN"));
      restaurant.setPriceRange(2);
      restaurant.setShippingCost(new Money(24, Currency.getInstance("MXN")));
      restaurant.setMinDeliveryTime(30);
      restaurant.setMaxDeliveryTime(40);
      restaurantManager.add(connection, restaurant);
      urlManager.add(connection, site, "pinchegringobbq.cdmx.menu", "pinchegringobbq");

      type = RestaurantTypeManager.getInstance().get("Argentina");
      restaurant = new Restaurant("tacosfondaargentina", "tacosfondaargentina.01.jpg", "Tacos Fonda Argentina", type, Currency.getInstance("MXN"));
      restaurant.setPriceRange(2);
      restaurant.setShippingCost(new Money(20, Currency.getInstance("MXN")));
      restaurant.setMinDeliveryTime(30);
      restaurant.setMaxDeliveryTime(35);
      restaurantManager.add(connection, restaurant);
      urlManager.add(connection, site, "tacosfondaargentina.cdmx.menu", "tacosfondaargentina");
    }
  }
}
