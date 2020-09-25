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
import net.cabezudo.sofia.core.schedule.Day;
import net.cabezudo.sofia.core.schedule.Hour;
import net.cabezudo.sofia.core.schedule.TimeEntriesTable;
import net.cabezudo.sofia.core.schedule.TimeTypeManager;
import net.cabezudo.sofia.core.schedule.TimeTypesTable;
import net.cabezudo.sofia.core.schedule.TimesTable;
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
import net.cabezudo.sofia.food.CategoriesTable;
import net.cabezudo.sofia.food.Category;
import net.cabezudo.sofia.food.CategoryManager;
import net.cabezudo.sofia.food.DishGroup;
import net.cabezudo.sofia.food.DishGroupManager;
import net.cabezudo.sofia.food.DishGroupsTable;
import net.cabezudo.sofia.food.DishManager;
import net.cabezudo.sofia.food.DishTable;
import net.cabezudo.sofia.languages.LanguagesTable;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.municipalities.MunicipalitiesTable;
import net.cabezudo.sofia.municipalities.Municipality;
import net.cabezudo.sofia.municipalities.MunicipalityManager;
import net.cabezudo.sofia.people.PeopleTable;
import net.cabezudo.sofia.phonenumbers.PhoneNumbersTable;
import net.cabezudo.sofia.postalcodes.PostalCodeManager;
import net.cabezudo.sofia.postalcodes.PostalCodesTable;
import net.cabezudo.sofia.restaurants.DeliveryRange;
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
      Database.createTable(connection, TimeEntriesTable.CREATION_QUERY);
      Database.createTable(connection, TimeTypesTable.CREATION_QUERY);
      Database.createTable(connection, TimesTable.CREATION_QUERY);
    }
  }

  private static void createHayQueComerDatabase() throws SQLException {
    Database.createDatabase("hayQueComer");
    try ( Connection connection = Database.getConnection("hayQueComer")) {
      Database.createTable(connection, RestaurantTypesTable.CREATION_QUERY);
      Database.createTable(connection, RestaurantsTable.CREATION_QUERY);
      Database.createTable(connection, CategoriesTable.CREATION_QUERY);
      Database.createTable(connection, DishGroupsTable.CREATION_QUERY);
      Database.createTable(connection, DishTable.CREATION_QUERY);
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
        createSites(startOptions);
        UserManager.getInstance().createAdministrator(startOptions);
        createData();
      }
    } catch (SQLException | IOException e) {
      throw new RuntimeConfigurationException("Error in system configuration listener.", e);
    }
  }

  private static void createData() throws SQLException {
    Country country = createCountries();
    createPostalCodes(country, null);
    createTimeTypes();
    createRestaurants();
  }

  private static void createSites(StartOptions startOptions) throws SQLException, IOException {
    Logger.info("Create sites.");
    if (System.console() != null && !startOptions.hasIDE()) {
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

  private static void createTimeTypes() throws SQLException {
    TimeTypeManager.getInstance().add("time");
    TimeTypeManager.getInstance().add("day");
    TimeTypeManager.getInstance().add("week");
    TimeTypeManager.getInstance().add("month");
    TimeTypeManager.getInstance().add("year");
  }

  private static void createRestaurants() throws SQLException {
    Logger.debug("Create restaurante data");

    URLManager urlManager = URLManager.getInstance();
    DomainNameManager domainNameManager = DomainNameManager.getInstance();
    String domainName;

    try ( Connection connection = Database.getConnection();) {
      Site site = SiteManager.getInstance().getByHostame(connection, "hayquecomer.com");

      RestaurantTypeManager restaurantTypeManager = RestaurantTypeManager.getInstance();

      restaurantTypeManager.add(connection, "Argentina");
      restaurantTypeManager.add(connection, "Postres");
      restaurantTypeManager.add(connection, "Americana");
      restaurantTypeManager.add(connection, "Hamburguesas");

      RestaurantType type;

      RestaurantManager restaurantManager = RestaurantManager.getInstance();
      Restaurant restaurant;

      String location = null;

      type = RestaurantTypeManager.getInstance().get("Argentina");
      restaurant = restaurantManager.add(
              connection, "donbeto", "donbeto/donbeto.jpg", "Parrillada Don Beto", location, type, 2, Currency.getInstance("MXN"),
              new Money(Currency.getInstance("MXN"), 25), new DeliveryRange(30)
      );
      domainName = "donbeto.cdmx.menu";
      urlManager.add(connection, site, domainName, "donbeto");
      domainNameManager.add(connection, site.getId(), domainName);
      createDonBetoDishes(restaurant);

      type = RestaurantTypeManager.getInstance().get("Hamburguesas");
      restaurant = restaurantManager.add(
              connection, "elgrilloh", "elgrilloh/elgrilloh.jpg", "El Grill Oh!", location, type, 2, Currency.getInstance("MXN"),
              new Money(Currency.getInstance("MXN"), 0), new DeliveryRange(30, 40)
      );
      domainName = "elgrilloh.cdmx.menu";
      urlManager.add(connection, site, domainName, "elgrilloh");
      domainNameManager.add(connection, site.getId(), domainName);
      createElGrillOhDishes(restaurant);

      restaurant = restaurantManager.add(
              connection, "bariloche", "bariloche/bariloche.jpg", "Bariloche", location, type, 2, Currency.getInstance("MXN"),
              new Money(Currency.getInstance("MXN"), 30), new DeliveryRange(35)
      );
      domainName = "bariloche.cdmx.menu";
      urlManager.add(connection, site, domainName, "bariloche");
      domainNameManager.add(connection, site.getId(), domainName);
      createBarilocheDishes(restaurant);

      restaurant = restaurantManager.add(
              connection, "casquet", "casquet/casquet.jpg", "Casquet", location, type, 2, Currency.getInstance("MXN"),
              new Money(Currency.getInstance("MXN"), 20), new DeliveryRange(30)
      );
      domainName = "casquet.cdmx.menu";
      urlManager.add(connection, site, domainName, "casquet");
      domainNameManager.add(connection, site.getId(), domainName);
      createCasquetDishes(restaurant);

      type = RestaurantTypeManager.getInstance().get("Postres");
      restaurant = restaurantManager.add(
              connection, "heladosdolphy", "heladosdolphy/heladosdolphy.jpg", "Helados Dolphy", "Dakota", type, 1, Currency.getInstance("MXN"),
              new Money(Currency.getInstance("MXN"), 18), new DeliveryRange(20, 30)
      );
      domainName = "heladosdolphy.cdmx.menu";
      urlManager.add(connection, site, domainName, "heladosdolphy");
      domainNameManager.add(connection, site.getId(), domainName);
      createheladosDolphyDishes(restaurant);

      type = RestaurantTypeManager.getInstance().get("Americana");
      restaurant = restaurantManager.add(
              connection, "pinchegringobbq", "pinchegringobbq/pinchegringobbq.jpg", "Pinche Gringo BBQ", location, type, 2, Currency.getInstance("MXN"),
              new Money(Currency.getInstance("MXN"), 24), new DeliveryRange(30, 40)
      );
      domainName = "pinchegringobbq.cdmx.menu";
      urlManager.add(connection, site, domainName, "pinchegringobbq");
      domainNameManager.add(connection, site.getId(), domainName);
      createPincheGringoBBQDishes(restaurant);

      type = RestaurantTypeManager.getInstance().get("Argentina");
      restaurant = restaurantManager.add(
              connection, "tacosfondaargentina", "tacosfondaargentina/tacosfondaargentina.jpg", "Tacos Fonda Argentina", location, type, 2, Currency.getInstance("MXN"),
              new Money(Currency.getInstance("MXN"), 20), new DeliveryRange(30, 35)
      );
      domainName = "tacosfondaargentina.cdmx.menu";
      urlManager.add(connection, site, domainName, "tacosfondaargentina");
      domainNameManager.add(connection, site.getId(), domainName);
      createTacosFondaArgentinaDishes(restaurant);
    }
  }

  private static void createDonBetoDishes(Restaurant restaurant) throws SQLException {
    CategoryManager categoryManager = CategoryManager.getInstance();
    DishGroupManager dishGroupManager = DishGroupManager.getInstance();
    DishManager dishManager = DishManager.getInstance();

    String noDescription = null;
    String noImageName = null;
    Integer noCalories = null;

    Category breakfast = categoryManager.add(restaurant, "Desayuno");
    categoryManager.add(breakfast, Day.MONDAY, new Hour(9, 30, 0), new Hour(13, 00, 0));
    categoryManager.add(breakfast, Day.TUESDAY, new Hour(7, 30, 0), new Hour(10, 00, 0));
    categoryManager.add(breakfast, Day.WEDNESDAY, new Hour(8, 10, 0), new Hour(15, 00, 0));
    categoryManager.add(breakfast, Day.SATURDAY, new Hour(9, 30, 0), new Hour(13, 00, 0));
    categoryManager.add(breakfast, Day.SUNDAY, new Hour(10, 30, 0), new Hour(12, 00, 0));

    Category menu = categoryManager.add(restaurant, "Menu");
    categoryManager.add(menu, Day.MONDAY, new Hour(12, 30, 0), new Hour(14, 15, 0));
    categoryManager.add(menu, Day.MONDAY, new Hour(16, 30, 0), new Hour(18, 15, 0));
    categoryManager.add(menu, Day.TUESDAY, new Hour(12, 30, 0), new Hour(14, 20, 0));
    categoryManager.add(menu, Day.TUESDAY, new Hour(15, 30, 0), new Hour(18, 15, 0));
    categoryManager.add(menu, Day.WEDNESDAY, new Hour(12, 30, 0), new Hour(15, 45, 0));
    categoryManager.add(menu, Day.WEDNESDAY, new Hour(16, 30, 0), new Hour(18, 15, 0));
    categoryManager.add(menu, Day.THURSDAY, new Hour(12, 30, 0), new Hour(18, 15, 0));
    categoryManager.add(menu, Day.FRIDAY, new Hour(12, 30, 0), new Hour(19, 15, 0));
    categoryManager.add(menu, Day.SATURDAY, new Hour(12, 30, 0), new Hour(14, 15, 0));
    categoryManager.add(menu, Day.SATURDAY, new Hour(16, 30, 0), new Hour(19, 15, 0));
    categoryManager.add(menu, Day.SUNDAY, new Hour(12, 30, 0), new Hour(14, 30, 0));
    categoryManager.add(menu, Day.SUNDAY, new Hour(15, 00, 0), new Hour(19, 15, 0));

    DishGroup chilaquiles = dishGroupManager.add(breakfast, "Chilaquiles");
    DishGroup sandwichesClasicos = dishGroupManager.add(breakfast, "Sandwiches Clásicos");
    DishGroup omelettes = dishGroupManager.add(breakfast, "Omelettes");
    DishGroup huevosAlGusto = dishGroupManager.add(breakfast, "Huevos al Gusto");
    DishGroup loDulce = dishGroupManager.add(breakfast, "Lo dulce");
    DishGroup empanadas = dishGroupManager.add(breakfast, "Empanadas");
    DishGroup caldos = dishGroupManager.add(breakfast, "Caldos");
    DishGroup paraEmpezarElDia = dishGroupManager.add(breakfast, "Para Empezar el Día");
    DishGroup bebidas = dishGroupManager.add(breakfast, "Bebidas");

    dishManager.add(chilaquiles, "Chilaquiles con Pollo", "Los mejores chilaquiles con pollo que se te pueden haber ocurrido probar en tu vida. Hasta tienen pollo de verdad y todo.", "donbeto/chilaquiles.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(chilaquiles, "Chilaquiles con Huevo", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(chilaquiles, "Chilaquiles con Chistorra", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 123));
    dishManager.add(chilaquiles, "Chilaquiles con Arrachera", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 131));

    dishManager.add(sandwichesClasicos, "Tostado", "Pan de campo con jamón, queso gratinado y mayonesas de la casa (pesto de arúgula), acompañado de huevos fritos.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(sandwichesClasicos, "Chivito", "Pan de rehilete con Arrachera, huevo frito, tocino, jamón, queso, lechuga, jitomate y cebolla.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 131));

    dishManager.add(omelettes, "Omelette de Jamón, Queso y Espinaca", noDescription, "donbeto/ommelette.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(omelettes, "Omelette de Champiñón, Queso y Cebolla Morada", noDescription, "donbeto/ommelette.02.jpg", noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(omelettes, "Omelette de Chistorra, Champiñón y Queso", noDescription, "donbeto/ommelette.03.jpg", noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(omelettes, "Omelette de Queso de Cabra, Jamón y Cebolla Morada", noDescription, "donbeto/ommelette.04.jpg", noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(omelettes, "Omelette de Salmón Ahumado, Queso de Cabra y Cebollín.", noDescription, "donbeto/ommelette.05.jpg", noCalories, new Money(Currency.getInstance("MXN"), 131)
    );

    dishManager.add(huevosAlGusto, "Huevos con Jamón", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(huevosAlGusto, "Huevos con Chorizo", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(huevosAlGusto, "Huevos con Tocino", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(huevosAlGusto, "Huevos con Queso", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(huevosAlGusto, "Huevos con Salsa", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 112));

    dishManager.add(loDulce, "Pan Fancés", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 112));

    dishManager.add(empanadas, "Empanada de Carne", noDescription, "donbeto/empanada.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 54));
    dishManager.add(empanadas, "Empanada de Chistorra", noDescription, "donbeto/empanada.02.jpg", noCalories, new Money(Currency.getInstance("MXN"), 54));
    dishManager.add(empanadas, "Empanada de Espinaca", noDescription, "donbeto/empanada.03.jpg", noCalories, new Money(Currency.getInstance("MXN"), 54));
    dishManager.add(empanadas, "Empanada de Humita", noDescription, "donbeto/empanada.04.jpg", noCalories, new Money(Currency.getInstance("MXN"), 54));

    dishManager.add(caldos, "Caldo Bueno", "Con pollo, verduras y queso panela.", "donbeto/caldo.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 102));
    dishManager.add(caldos, "Jugo de Carne", noDescription, "donbeto/caldo.02.jpg", noCalories, new Money(Currency.getInstance("MXN"), 112));

    dishManager.add(paraEmpezarElDia, "Jugo de Naranja", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 36));
    dishManager.add(paraEmpezarElDia, "Pan Tostado con mantequilla y mermelada", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 19));
    dishManager.add(paraEmpezarElDia, "Plato de Frutas", "Con yogurt natural, miel y granola.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 72));

    dishManager.add(bebidas, "Naranjada", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 48));
    dishManager.add(bebidas, "Limonada", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 48));
    dishManager.add(bebidas, "Agua Embotellada", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 40));
    dishManager.add(bebidas, "Refresco", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 40));
    dishManager.add(bebidas, "Café", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 35));
    dishManager.add(bebidas, "Té", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 39));
    dishManager.add(bebidas, "Agua de Sabor", "Elige entre Horchata, Guanábana o Maracuyá.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 53));
    dishManager.add(bebidas, "Clericot", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 85));

    DishGroup entradas = dishGroupManager.add(menu, "Entradas");
    DishGroup sopas = dishGroupManager.add(menu, "Sopas");
    DishGroup deLaParrilla = dishGroupManager.add(menu, "De la Parrilla");
    DishGroup delMar = dishGroupManager.add(menu, "Del Mar");
    DishGroup ensaladas = dishGroupManager.add(menu, "Ensaladas");
    DishGroup guarniciones = dishGroupManager.add(menu, "Guarniciones");
    DishGroup platillos = dishGroupManager.add(menu, "Platillos");
    DishGroup pastas = dishGroupManager.add(menu, "Pastas");
    DishGroup postres = dishGroupManager.add(menu, "Postres");
    DishGroup bebidasEnMenu = dishGroupManager.add(menu, "Bebidas");

    dishManager.add(entradas, "Empanada de Carne", noDescription, "donbeto/entradas.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 54));
    dishManager.add(entradas, "Empanada de Chistorra", noDescription, "donbeto/entradas.02.jpg", noCalories, new Money(Currency.getInstance("MXN"), 54));
    dishManager.add(entradas, "Empanada de Humita", noDescription, "donbeto/entradas.03.jpg", noCalories, new Money(Currency.getInstance("MXN"), 54));
    dishManager.add(entradas, "Empanada de Humita", noDescription, "donbeto/entradas.04.jpg", noCalories, new Money(Currency.getInstance("MXN"), 54));
    dishManager.add(entradas, "Chorizo Argentino", noDescription, "donbeto/entradas.05.jpg", noCalories, new Money(Currency.getInstance("MXN"), 83));
    dishManager.add(entradas, "Chistorra a la Parrilla", noDescription, "donbeto/entradas.06.jpg", noCalories, new Money(Currency.getInstance("MXN"), 95));
    dishManager.add(entradas, "Pulpo Romana", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 174));
    dishManager.add(entradas, "Tostadas de Atún Fresco (2pz)", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 122));
    dishManager.add(entradas, "Panela con Champiñon al Ajillo", noDescription, "donbeto/entradas.09.jpg", noCalories, new Money(Currency.getInstance("MXN"), 113));
    dishManager.add(entradas, "Queso Fundido", noDescription, "donbeto/entradas.10.jpg", noCalories, new Money(Currency.getInstance("MXN"), 106));
    dishManager.add(entradas, "Queso Fundido con Chistorra", noDescription, "donbeto/entradas.11.jpg", noCalories, new Money(Currency.getInstance("MXN"), 124));
    dishManager.add(entradas, "Queso Provoleta", noDescription, "donbeto/entradas.12.jpg", noCalories, new Money(Currency.getInstance("MXN"), 127));
    dishManager.add(entradas, "Provoleta Relleno", "Relleno de arúgula, jitomate deshidratado, tocino y chipotle.", "donbeto/entradas.13.jpg", noCalories, new Money(Currency.getInstance("MXN"), 160));
    dishManager.add(entradas, "Mollejas de Res a la Parrilla", noDescription, "donbeto/entradas.14.jpg", noCalories, new Money(Currency.getInstance("MXN"), 116));
    dishManager.add(entradas, "Mollejas al Verdeo", "Mollejas de res a la parrilla bañada de una exquisita salsa a base de un jugo de carne, cebollín y vino blanco.", "donbeto/entradas.15.jpg", noCalories, new Money(Currency.getInstance("MXN"), 168));

    dishManager.add(sopas, "Jugo de Carne", noDescription, "donbeto/sopas.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(sopas, "Crema a los Cuatro Quesos", "Deliciosa mezcla de roquefort, parmesano, edam y machego.", "donbeto/sopas.02.jpg", noCalories, new Money(Currency.getInstance("MXN"), 116));
    dishManager.add(sopas, "Caldito Bueno", "Con pollo, verduras y queso panela.", "donbeto/sopas.03.jpg", noCalories, new Money(Currency.getInstance("MXN"), 102));
    dishManager.add(sopas, "Sopa Campirana", "Exquisita crema con elote, chile poblano y queso.", "donbeto/sopas.04.jpg", noCalories, new Money(Currency.getInstance("MXN"), 107));

    dishManager.add(deLaParrilla, "Bife de Chorizo", "300 gramos.", "donbeto/deLaParrilla.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 276));
    dishManager.add(deLaParrilla, "Picaña", "300 gramos. Acompañada de puré de papa y gravy especial.", "donbeto/deLaParrilla.02.jpg", noCalories, new Money(Currency.getInstance("MXN"), 288));
    dishManager.add(deLaParrilla, "Vacío", "300 gramos.", "donbeto/deLaParrilla.03.jpg", noCalories, new Money(Currency.getInstance("MXN"), 315));
    dishManager.add(deLaParrilla, "Arrachera", "300 gramos.", "donbeto/deLaParrilla.04.jpg", noCalories, new Money(Currency.getInstance("MXN"), 268));
    dishManager.add(deLaParrilla, "Lomo", "300 gramos.", "donbeto/deLaParrilla.05.jpg", noCalories, new Money(Currency.getInstance("MXN"), 308));
    dishManager.add(deLaParrilla, "Asado de Tira", "400 gramos.", "donbeto/deLaParrilla.06.jpg", noCalories, new Money(Currency.getInstance("MXN"), 251));
    dishManager.add(deLaParrilla, "Pollo a la Parrilla", "Orégano, ajo, chile y ajillo.", "donbeto/deLaParrilla.07.jpg", noCalories, new Money(Currency.getInstance("MXN"), 196));
    dishManager.add(deLaParrilla, "Parrillada", "Arrachera, t-bone, asado de tira, pollo a elegir y chorizo argentino.", "donbeto/deLaParrilla.08.jpg", noCalories, new Money(Currency.getInstance("MXN"), 574));
    dishManager.add(deLaParrilla, "Parrillada Don Beto", "Bife de chorizo, arrachera, churrasco, asado de tira, pollo a elegir, verduras asadas, queso panela y chorizo argentino.", "donbeto/deLaParrilla.09.jpg", noCalories, new Money(Currency.getInstance("MXN"), 753));

    dishManager.add(delMar, "Salmón a la Parrilla", noDescription, "donbeto/delMar.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 230));
    dishManager.add(delMar, "Atún Sellado", "En costra de ajonjolí negro.", "donbeto/delMar.02.jpg", noCalories, new Money(Currency.getInstance("MXN"), 245));
    dishManager.add(delMar, "Filete de Pescado a la Livornese", "Delicioso Filete de Pescado con jitomate, alcaparras, aceitunas negras y vino blanco. Acompañado de pasta al burro.", "donbeto/delMar.03.jpg", noCalories, new Money(Currency.getInstance("MXN"), 212));
    dishManager.add(delMar, "Filete de Pescado a la Putanesca", "En salsa con tomate, anchoas, aceitunas, orégano y una pizca guindillas. Acompañado de pasta al burro.", "donbeto/delMar.04.jpg", noCalories, new Money(Currency.getInstance("MXN"), 212));
    dishManager.add(delMar, "Filete de Pescado Piccata", "Prepara con una deliciosa salsa a base de mantequilla, limón y especias. Acompañado de pasta al burro.", "donbeto/delMar.05.jpg", noCalories, new Money(Currency.getInstance("MXN"), 212));
    dishManager.add(delMar, "Pulpo a la Parrilla", "Con papa bábara y adobo de la casa.", "donbeto/delMar.06.jpg", noCalories, new Money(Currency.getInstance("MXN"), 332));

    dishManager.add(ensaladas, "Ensalada Tango", "Espinaca, berro, fresa, queso provoleta y tocino.", "donbeto/ensalada.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 126));
    dishManager.add(ensaladas, "Ensalada Gaucha", "Espinaca, lechuga. aguacate, jitomate cherry, apio, aceitunas negras y queso de cabra.", "donbeto/ensalada.02.jpg", noCalories, new Money(Currency.getInstance("MXN"), 128));
    dishManager.add(ensaladas, "Ensalada Don José", "Mezcla de lechuga, manzana, espinaca, berro, germen de soya, queso camembert y amaranto.", "donbeto/ensalada.03.jpg", noCalories, new Money(Currency.getInstance("MXN"), 126));
    dishManager.add(ensaladas, "Ensalada Caminito", "Trocitos de atún sellado, lechuga escarola, arúgula, berro, queso parmesano y germen de soya.", "donbeto/ensalada.04.jpg", noCalories, new Money(Currency.getInstance("MXN"), 238));
    dishManager.add(ensaladas, "Ensalada Mixta", "Mezcla de lechugas, jitomate, zanahoria y cebolla.", "donbeto/ensalada.05.jpg", noCalories, new Money(Currency.getInstance("MXN"), 92));
    dishManager.add(ensaladas, "Ensalada de la Casa", "Mezcla de lechugas, berro, espinaca, apio, betabel, champiñón huevo y tocino.", "donbeto/ensalada.06.jpg", noCalories, new Money(Currency.getInstance("MXN"), 102));
    dishManager.add(ensaladas, "Ensalada Gaucha con Pechuga a la Parrilla", "Espinaca, Lechuga, Aguacate, Jitomate Cherry, Apio, Aceituna Verde, Queso de Cabra y Tiras de Pollo a la Parrilla.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 188));

    dishManager.add(guarniciones, "Papas a la Francesa", noDescription, "donbeto/guarnicion.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 83));
    dishManager.add(guarniciones, "Espinacas con Crema", noDescription, "donbeto/guarnicion.02.jpg", noCalories, new Money(Currency.getInstance("MXN"), 87));
    dishManager.add(guarniciones, "Verduras Asadas", noDescription, "donbeto/guarnicion.03.jpg", noCalories, new Money(Currency.getInstance("MXN"), 94));
    dishManager.add(guarniciones, "Puré de Papa", noDescription, "donbeto/guarnicion.04.jpg", noCalories, new Money(Currency.getInstance("MXN"), 94));
    dishManager.add(guarniciones, "Papa al Horno", noDescription, "donbeto/guarnicion.05.jpg", noCalories, new Money(Currency.getInstance("MXN"), 102));
    dishManager.add(guarniciones, "Champiñones al Ajillo", noDescription, "donbeto/guarnicion.06.jpg", noCalories, new Money(Currency.getInstance("MXN"), 96));

    dishManager.add(platillos, "Milanesa de Res", "Con papas a la francesa y ensalada.", "donbeto/platillo.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 161));
    dishManager.add(platillos, "Milanesa de Pollo", "Con papa a la francesa y ensalada.", "donbeto/platillo.02.jpg", noCalories, new Money(Currency.getInstance("MXN"), 147));
    dishManager.add(platillos, "Milanesa Napolitana", "Con salsa de jitomate, jamón y queso gratinado. Con papas a la francesa y ensalada.", "donbeto/platillo.03.jpg", noCalories, new Money(Currency.getInstance("MXN"), 198));
    dishManager.add(platillos, "Choripan", noDescription, "donbeto/platillo.04.jpg", noCalories, new Money(Currency.getInstance("MXN"), 116));
    dishManager.add(platillos, "Choripan con Queso", noDescription, "donbeto/platillo.05.jpg", noCalories, new Money(Currency.getInstance("MXN"), 134));
    dishManager.add(platillos, "Chapata de Arrachera", "Con papas a la francesa.", "donbeto/platillo.06.jpg", noCalories, new Money(Currency.getInstance("MXN"), 147));
    dishManager.add(platillos, "Hamburguesa", "Con queso y papas a la francesa.", "donbeto/platillo.07.jpg", noCalories, new Money(Currency.getInstance("MXN"), 153));
    dishManager.add(platillos, "Hamburguesa Especial", "Con queso, jamón, tocino y papas a la francesa.", "donbeto/platillo.08.jpg", noCalories, new Money(Currency.getInstance("MXN"), 183));
    dishManager.add(platillos, "Hamburguesa Costanera", "Con queso, chorizo argentino y papas a la francesa.", "donbeto/platillo.09.jpg", noCalories, new Money(Currency.getInstance("MXN"), 187));

    dishManager.add(pastas, "Pasta Alfredo", noDescription, "donbeto/pasta.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 147));
    dishManager.add(pastas, "Pasta Pesto", noDescription, "donbeto/pasta.02.jpg", noCalories, new Money(Currency.getInstance("MXN"), 147));
    dishManager.add(pastas, "Pasta Burro", noDescription, "donbeto/pasta.03.jpg", noCalories, new Money(Currency.getInstance("MXN"), 147));
    dishManager.add(pastas, "Pasta Boloñesa", noDescription, "donbeto/pasta.04.jpg", noCalories, new Money(Currency.getInstance("MXN"), 181));
    dishManager.add(pastas, "Pasta Arrabiata", "Con chorizo argentino.", "donbeto/pasta.05.jpg", noCalories, new Money(Currency.getInstance("MXN"), 181));
    dishManager.add(pastas, "Pasta de Salmón en Salsa Rosada", noDescription, "donbeto/pasta.06.jpg", noCalories, new Money(Currency.getInstance("MXN"), 198));

    dishManager.add(postres, "Crepas de Dulce de Leche", "Con Helado de Vainilla", "donbeto/postre.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 74));
    dishManager.add(postres, "Crepa de Nutella", noDescription, "donbeto/postre.02.jpg", noCalories, new Money(Currency.getInstance("MXN"), 74));
    dishManager.add(postres, "Crepas Don Beto", "Queso Philadelphia, Plátano y Fresa. Con Helado de Vainilla.", "donbeto/postre.03.jpg", noCalories, new Money(Currency.getInstance("MXN"), 98));
    dishManager.add(postres, "Helado Frito Don Beto", "Delicioso Helado de Vainilla cubierto de una esponjosita masa frita. Bañado con jarabe de chocolate.", "donbeto/postre.04.jpg", noCalories, new Money(Currency.getInstance("MXN"), 82));
    dishManager.add(postres, "Brownie con Helado", noDescription, "donbeto/postre.05.jpg", noCalories, new Money(Currency.getInstance("MXN"), 77));
    dishManager.add(postres, "Strudel de Manzana", "Calientito y con helado de vainilla.", "donbeto/postre.06.jpg", noCalories, new Money(Currency.getInstance("MXN"), 77));
    dishManager.add(postres, "Alfajor", "De dulce de leche con nuez.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 52));

    dishManager.add(bebidasEnMenu, "Naranjada o Limonada", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 48));
    dishManager.add(bebidasEnMenu, "Agua Embotellada", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 40));
    dishManager.add(bebidasEnMenu, "Refresco", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 40));
    dishManager.add(bebidasEnMenu, "Café", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 35));
    dishManager.add(bebidasEnMenu, "Té", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 39));
    dishManager.add(bebidasEnMenu, "Agua de Sabor", "Elige entre Horchata, Guanábana o Maracuyá.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 53));
    dishManager.add(bebidasEnMenu, "Clericot", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 85));
  }

  private static void createElGrillOhDishes(Restaurant restaurant) throws SQLException {
    CategoryManager categoryManager = CategoryManager.getInstance();
    DishGroupManager dishGroupManager = DishGroupManager.getInstance();
    DishManager dishManager = DishManager.getInstance();

    String noDescription = null;
    String noImageName = null;
    Integer noCalories = null;

    Category menu = categoryManager.add(restaurant, "Menu");
    categoryManager.add(menu, Day.MONDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.TUESDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.WEDNESDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.THURSDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.FRIDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.SATURDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.SUNDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));

    DishGroup combos = dishGroupManager.add(menu, "Combos");
    DishGroup hamburguesas = dishGroupManager.add(menu, "Hamburguesas");
    DishGroup otrosPlatillos = dishGroupManager.add(menu, "Otros Platillos");
    DishGroup ensaladas = dishGroupManager.add(menu, "Ensaladas");

    dishManager.add(combos, "Clásica", "A la parrilla con queso", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 70));
    dishManager.add(combos, "Hawaiana", "Con jamón y piña", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 80));
    dishManager.add(combos, "Ranchera", "Con aguacate y tocino", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 80));
    dishManager.add(combos, "BBQ", "Con salsa especial BBQ", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 80));
    dishManager.add(combos, "Champiñon a la Parrilla", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 80));
    dishManager.add(combos, "4 Quesos", "Blue cheese, manchego, oaxaca y americano", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 84));
    dishManager.add(combos, "Chelita", "A la parrilla con salsa de cerveza y tocino", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 84));
    dishManager.add(combos, "Meditarránea", "Marinada con vino tinto y cebolla caramelizada", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 84));
    dishManager.add(combos, "Whiskey BBQ", "A la parrilla con salsa BBQ y whiskey", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 84));
    dishManager.add(combos, "Hot Dog", "Con papas y bebida", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 60));
    dishManager.add(combos, "Hot Dog Jumbo Clásico", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 75));
    dishManager.add(combos, "Hot Dog Hawaiano", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 80));
    dishManager.add(combos, "Hot Dog Ranchero", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 80));

    dishManager.add(hamburguesas, "Hongo Portobello", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 50));
    dishManager.add(hamburguesas, "Pollo", "100 gramos.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 40));
    dishManager.add(hamburguesas, "Angus", "110 gramos.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 50));
    dishManager.add(hamburguesas, "Sirloin", "160 gramos.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 55));

    dishManager.add(otrosPlatillos, "Nachos con Queso", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 30));
    dishManager.add(otrosPlatillos, "Nachos con Carne", "2 personas", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 65));
    dishManager.add(otrosPlatillos, "Aros de Cebolla", "12 piezas", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 39));
    dishManager.add(otrosPlatillos, "Papas a la Francesa", "250 gramos.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 35));
    dishManager.add(otrosPlatillos, "Papas Gajo", "250 gramos.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 45));
    dishManager.add(otrosPlatillos, "Nuggets de Pollo", "6 piezas.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 30));
    dishManager.add(otrosPlatillos, "Dedos de Queso", "5 piezas.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 55));
    dishManager.add(otrosPlatillos, "Plato Appetizer", "Papas gajo, aros de cebolla, dedos de queso y nuggets", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 95));
    dishManager.add(otrosPlatillos, "Hot Dog con Queso", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 25));
    dishManager.add(otrosPlatillos, "Hot Dog Jumbo Clásico", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 50));
    dishManager.add(otrosPlatillos, "Alitas de Pollo", "BBQ y picositas. 5 piezas.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 65));
    dishManager.add(otrosPlatillos, "Tiras de Pollo BBQ", "4 piezas", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 65));

    dishManager.add(ensaladas, "Ensalada de la casa", "Lechuga, jitomate, queso mozzarella, ajonjolí garrapiñado, queso manchego, jamón, de pabo, arándanos y crotones.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 60));
    dishManager.add(ensaladas, "Ensalada con Portobello Gratinado", "Lechuga, jitomate, arándanos, portobello gratinado con queso mozzarella, crotones y ajonjolí garrapiñado.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 70));
    dishManager.add(ensaladas, "Ensalada con Pechuga a la Parilla", "Lechuga, jitomate, arándanos, pollo a las finas hierbas, queso mozzarella, crotones y ajonjolí garrapiñado.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 70));
  }

  private static void createBarilocheDishes(Restaurant restaurant) throws SQLException {
    CategoryManager categoryManager = CategoryManager.getInstance();
    DishGroupManager dishGroupManager = DishGroupManager.getInstance();
    DishManager dishManager = DishManager.getInstance();

    String noDescription = null;
    String noImageName = null;
    Integer noCalories = null;

    Category breakfast = categoryManager.add(restaurant, "Desayuno");
    categoryManager.add(breakfast, Day.MONDAY, new Hour(8, 30, 0), new Hour(14, 0, 0));
    categoryManager.add(breakfast, Day.TUESDAY, new Hour(8, 30, 0), new Hour(14, 0, 0));
    categoryManager.add(breakfast, Day.WEDNESDAY, new Hour(8, 30, 0), new Hour(12, 0, 0));
    categoryManager.add(breakfast, Day.THURSDAY, new Hour(8, 30, 0), new Hour(12, 0, 0));
    categoryManager.add(breakfast, Day.FRIDAY, new Hour(8, 30, 0), new Hour(12, 0, 0));
    categoryManager.add(breakfast, Day.SATURDAY, new Hour(8, 30, 0), new Hour(12, 0, 0));
    categoryManager.add(breakfast, Day.SUNDAY, new Hour(8, 30, 0), new Hour(12, 0, 0));

    Category menu = categoryManager.add(restaurant, "Menu");
    categoryManager.add(menu, Day.MONDAY, new Hour(18, 30, 0), new Hour(24, 0, 0));
    categoryManager.add(menu, Day.TUESDAY, new Hour(18, 30, 0), new Hour(24, 0, 0));
    categoryManager.add(menu, Day.WEDNESDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.THURSDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.FRIDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.SATURDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.SUNDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));

    DishGroup eggs = dishGroupManager.add(breakfast, "Huevos");
    dishManager.add(eggs, "Huevo estrellado", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 20));

    DishGroup hamburguesas = dishGroupManager.add(menu, "Hamburguesas");
    dishManager.add(hamburguesas, "Clásica", "A la parrilla con queso", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 70));
  }

  private static void createCasquetDishes(Restaurant restaurant) throws SQLException {
    CategoryManager categoryManager = CategoryManager.getInstance();
    DishGroupManager dishGroupManager = DishGroupManager.getInstance();
    DishManager dishManager = DishManager.getInstance();

    String noDescription = null;
    String noImageName = null;
    Integer noCalories = null;

    Category menu = categoryManager.add(restaurant, "Menu");
    categoryManager.add(menu, Day.MONDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.TUESDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.WEDNESDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.THURSDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.FRIDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.SATURDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.SUNDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));

    DishGroup combos = dishGroupManager.add(menu, "Combos");

    dishManager.add(combos, "Clásica", "A la parrilla con queso", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 70));
  }

  private static void createheladosDolphyDishes(Restaurant restaurant) throws SQLException {
    CategoryManager categoryManager = CategoryManager.getInstance();
    DishGroupManager dishGroupManager = DishGroupManager.getInstance();
    DishManager dishManager = DishManager.getInstance();

    String noDescription = null;
    String noImageName = null;
    Integer noCalories = null;

    Category menu = categoryManager.add(restaurant, "Menu");
    categoryManager.add(menu, Day.MONDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.TUESDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.WEDNESDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.THURSDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.FRIDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.SATURDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.SUNDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));

    DishGroup combos = dishGroupManager.add(menu, "Combos");

    dishManager.add(combos, "Clásica", "A la parrilla con queso", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 70));
  }

  private static void createPincheGringoBBQDishes(Restaurant restaurant) throws SQLException {
    CategoryManager categoryManager = CategoryManager.getInstance();
    DishGroupManager dishGroupManager = DishGroupManager.getInstance();
    DishManager dishManager = DishManager.getInstance();

    String noDescription = null;
    String noImageName = null;
    Integer noCalories = null;

    Category menu = categoryManager.add(restaurant, "Menu");
    categoryManager.add(menu, Day.MONDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.TUESDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.WEDNESDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.THURSDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.FRIDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.SATURDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.SUNDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));

    DishGroup combos = dishGroupManager.add(menu, "Combos");

    dishManager.add(combos, "Clásica", "A la parrilla con queso", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 70));
  }

  private static void createTacosFondaArgentinaDishes(Restaurant restaurant) throws SQLException {
    CategoryManager categoryManager = CategoryManager.getInstance();
    DishGroupManager dishGroupManager = DishGroupManager.getInstance();
    DishManager dishManager = DishManager.getInstance();

    String noDescription = null;
    String noImageName = null;
    Integer noCalories = null;

    Category menu = categoryManager.add(restaurant, "Menu");
    categoryManager.add(menu, Day.MONDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.TUESDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.WEDNESDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.THURSDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.FRIDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.SATURDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));
    categoryManager.add(menu, Day.SUNDAY, new Hour(18, 30, 0), new Hour(22, 0, 0));

    DishGroup combos = dishGroupManager.add(menu, "Combos");

    dishManager.add(combos, "Clásica", "A la parrilla con queso", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 70));
  }
}
