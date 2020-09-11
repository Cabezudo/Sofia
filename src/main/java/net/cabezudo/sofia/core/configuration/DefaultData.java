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

      String location = null;

      type = RestaurantTypeManager.getInstance().get("Argentina");
      restaurant = restaurantManager.add(
              connection, "donbeto", "donbeto.01.jpg", "Parrillada Don Beto", location, type, 2, Currency.getInstance("MXN"),
              new Money(Currency.getInstance("MXN"), 25), new DeliveryRange(30)
      );

      urlManager.add(connection, site, "donbeto.cdmx.menu", "donbeto");
      createDonBetoDishes(restaurant);

      restaurant = restaurantManager.add(
              connection, "bariloche", "bariloche.01.jpg", "Bariloche", location, type, 2, Currency.getInstance("MXN"),
              new Money(Currency.getInstance("MXN"), 30), new DeliveryRange(35)
      );
      urlManager.add(connection, site, "bariloche.cdmx.menu", "bariloche");

      restaurant = restaurantManager.add(
              connection, "casquet", "casquet.01.jpg", "Casquet", location, type, 2, Currency.getInstance("MXN"),
              new Money(Currency.getInstance("MXN"), 20), new DeliveryRange(30)
      );
      urlManager.add(connection, site, "casquet.cdmx.menu", "casquet");

      type = RestaurantTypeManager.getInstance().get("Postres");
      restaurant = restaurantManager.add(
              connection, "heladosdolphy", "heladosdolphy.01.jpg", "Helados Dolphy", "Dakota", type, 1, Currency.getInstance("MXN"),
              new Money(Currency.getInstance("MXN"), 18), new DeliveryRange(20, 30)
      );
      urlManager.add(connection, site, "heladosdolphy.cdmx.menu", "heladosdolphy");

      type = RestaurantTypeManager.getInstance().get("Americana");
      restaurant = restaurantManager.add(
              connection, "pinchegringobbq", "pinchegringobbq.01.jpg", "Pinche Gringo BBQ", location, type, 2, Currency.getInstance("MXN"),
              new Money(Currency.getInstance("MXN"), 24), new DeliveryRange(30, 40)
      );
      urlManager.add(connection, site, "pinchegringobbq.cdmx.menu", "pinchegringobbq");

      type = RestaurantTypeManager.getInstance().get("Argentina");
      restaurant = restaurantManager.add(
              connection, "tacosfondaargentina", "tacosfondaargentina.01.jpg", "Tacos Fonda Argentina", location, type, 2, Currency.getInstance("MXN"),
              new Money(Currency.getInstance("MXN"), 20), new DeliveryRange(30, 35)
      );
      urlManager.add(connection, site, "tacosfondaargentina.cdmx.menu", "tacosfondaargentina");
    }
  }

  private static void createDonBetoDishes(Restaurant restaurant) throws SQLException {
    CategoryManager categoryManager = CategoryManager.getInstance();
    DishGroupManager dishGroupManager = DishGroupManager.getInstance();
    DishManager dishManager = DishManager.getInstance();

    int noSelection = 0;
    String noDescription = null;
    String noImageName = null;
    String noCode = null;
    Integer noCalories = null;

    Category breakfast = categoryManager.add(restaurant, "Desayuno");
    Category menu = categoryManager.add(restaurant, "Menu");

    DishGroup chilaquiles = dishGroupManager.add(breakfast, "Chilaquiles");
    DishGroup sandwichesClasicos = dishGroupManager.add(breakfast, "Sandwiches Clásicos");
    DishGroup omelettes = dishGroupManager.add(breakfast, "Omelettes");
    DishGroup huevosAlGusto = dishGroupManager.add(breakfast, "Huevos al Gusto");
    DishGroup loDulce = dishGroupManager.add(breakfast, "Lo dulce");
    DishGroup empanadas = dishGroupManager.add(breakfast, "Empanadas");
    DishGroup caldos = dishGroupManager.add(breakfast, "Caldos");
    DishGroup paraEmpezarElDia = dishGroupManager.add(breakfast, "Para Empezar el Día");
    DishGroup bebidas = dishGroupManager.add(breakfast, "Bebidas");

    dishManager.add(chilaquiles, "Chilaquiles con Pollo", "Los mejores chilaquiles con pollo que se te pueden haber ocurrido probar en tu vida. Hasta tienen pollo de verdad y todo.", "chilaquiles.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(chilaquiles, "Chilaquiles con Huevo", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(chilaquiles, "Chilaquiles con Chistorra", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 123));
    dishManager.add(chilaquiles, "Chilaquiles con Arrachera", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 131));

    dishManager.add(sandwichesClasicos, "Tostado", "Pan de campo con jamón, queso gratinado y mayonesas de la casa (pesto de arúgula), acompañado de huevos fritos.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(sandwichesClasicos, "Chivito", "Pan de rehilete con Arrachera, huevo frito, tocino, jamón, queso, lechuga, jitomate y cebolla.", noImageName, noCalories, new Money(Currency.getInstance("MXN"), 131));

    dishManager.add(omelettes, "Omelette de Jamón, Queso y Espinaca", noDescription, "ommelette.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(omelettes, "Omelette de Champiñón, Queso y Cebolla Morada", noDescription, "ommelette.02.jpg", noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(omelettes, "Omelette de Chistorra, Champiñón y Queso", noDescription, "ommelette.03.jpg", noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(omelettes, "Omelette de Queso de Cabra, Jamón y Cebolla Morada", noDescription, "ommelette.04.jpg", noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(omelettes, "Omelette de Salmón Ahumado, Queso de Cabra y Cebollín.", noDescription, "ommelette.05.jpg", noCalories, new Money(Currency.getInstance("MXN"), 131)
    );

    dishManager.add(huevosAlGusto, "Huevos con Jamón", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(huevosAlGusto, "Huevos con Chorizo", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(huevosAlGusto, "Huevos con Tocino", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(huevosAlGusto, "Huevos con Queso", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(huevosAlGusto, "Huevos con Salsa", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 112));

    dishManager.add(loDulce, "Pan Fancés", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 112));

    dishManager.add(empanadas, "Empanada de Carne", noDescription, "empanada.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 54));
    dishManager.add(empanadas, "Empanada de Chistorra", noDescription, "empanada.02.jpg", noCalories, new Money(Currency.getInstance("MXN"), 54));
    dishManager.add(empanadas, "Empanada de Espinaca", noDescription, "empanada.03.jpg", noCalories, new Money(Currency.getInstance("MXN"), 54));
    dishManager.add(empanadas, "Empanada de Humita", noDescription, "empanada.04.jpg", noCalories, new Money(Currency.getInstance("MXN"), 54));

    dishManager.add(caldos, "Caldo Bueno", "Con pollo, verduras y queso panela.", "caldo.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 102));
    dishManager.add(caldos, "Jugo de Carne", noDescription, "caldo.02.jpg", noCalories, new Money(Currency.getInstance("MXN"), 112));

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

    dishManager.add(entradas, "Empanada de Carne", noDescription, "entradas.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 54));
    dishManager.add(entradas, "Empanada de Chistorra", noDescription, "entradas.02.jpg", noCalories, new Money(Currency.getInstance("MXN"), 54));
    dishManager.add(entradas, "Empanada de Humita", noDescription, "entradas.03.jpg", noCalories, new Money(Currency.getInstance("MXN"), 54));
    dishManager.add(entradas, "Empanada de Humita", noDescription, "entradas.04.jpg", noCalories, new Money(Currency.getInstance("MXN"), 54));
    dishManager.add(entradas, "Chorizo Argentino", noDescription, "entradas.05.jpg", noCalories, new Money(Currency.getInstance("MXN"), 83));
    dishManager.add(entradas, "Chistorra a la Parrilla", noDescription, "entradas.06.jpg", noCalories, new Money(Currency.getInstance("MXN"), 95));
    dishManager.add(entradas, "Pulpo Romana", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 174));
    dishManager.add(entradas, "Tostadas de Atún Fresco (2pz)", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 122));
    dishManager.add(entradas, "Panela con Champiñon al Ajillo", noDescription, "entradas.09.jpg", noCalories, new Money(Currency.getInstance("MXN"), 113));
    dishManager.add(entradas, "Queso Fundido", noDescription, "entradas.10.jpg", noCalories, new Money(Currency.getInstance("MXN"), 106));
    dishManager.add(entradas, "Queso Fundido con Chistorra", noDescription, "entradas.11.jpg", noCalories, new Money(Currency.getInstance("MXN"), 124));
    dishManager.add(entradas, "Queso Provoleta", noDescription, "entradas.12.jpg", noCalories, new Money(Currency.getInstance("MXN"), 127));
    dishManager.add(entradas, "Provoleta Relleno", "Relleno de arúgula, jitomate deshidratado, tocino y chipotle.", "entradas.13.jpg", noCalories, new Money(Currency.getInstance("MXN"), 160));
    dishManager.add(entradas, "Mollejas de Res a la Parrilla", noDescription, "entradas.14.jpg", noCalories, new Money(Currency.getInstance("MXN"), 116));
    dishManager.add(entradas, "Mollejas al Verdeo", "Mollejas de res a la parrilla bañada de una exquisita salsa a base de un jugo de carne, cebollín y vino blanco.", "entradas.15.jpg", noCalories, new Money(Currency.getInstance("MXN"), 168));

    dishManager.add(sopas, "Jugo de Carne", noDescription, "sopas.01.jpg", noCalories, new Money(Currency.getInstance("MXN"), 112));
    dishManager.add(sopas, "Crema a los Cuatro Quesos", "Deliciosa mezcla de roquefort, parmesano, edam y machego.", "sopas.02.jpg", noCalories, new Money(Currency.getInstance("MXN"), 116));
    dishManager.add(sopas, "Caldito Bueno", "Con pollo, verduras y queso panela.", "sopas.03.jpg", noCalories, new Money(Currency.getInstance("MXN"), 102));
    dishManager.add(sopas, "Sopa Campirana", "Exquisita crema con elote, chile poblano y queso.", "sopas.04.jpg", noCalories, new Money(Currency.getInstance("MXN"), 107));

    dishManager.add(deLaParrilla, "", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 1));

    dishManager.add(delMar, "", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 1));

    dishManager.add(ensaladas, "", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 1));

    dishManager.add(guarniciones, "", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 1));

    dishManager.add(platillos, "", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 1));

    dishManager.add(pastas, "", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 1));

    dishManager.add(postres, "", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 1));

    dishManager.add(bebidasEnMenu, "", noDescription, noImageName, noCalories, new Money(Currency.getInstance("MXN"), 1));

  }
}
