package net.cabezudo.sofia.core.configuration;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.26
 */
public class TestData {

//  private TestData() {
//    // Nothing to do here. Utility classes should not have public constructors.
//  }
//
//  private static final boolean CREATE_DATABASE = false;
//  private static final boolean CREATE_LESS_DATA = true;
//  private static final boolean RESTORE_DATABASE = false;
//  private static final Random random = new Random();
//
//  public static void create(StartOptions startOptions) throws EMailNotExistException, FileNotFoundException, UserNotExistException, ConfigurationException, DatabaseException {
//    try {
//      Logger.info("Load the JDBC driver %s.", Configuration.getInstance().getDatabaseDriver());
//      Class.forName(Configuration.getInstance().getDatabaseDriver());
//    } catch (ClassNotFoundException e) {
//      throw new ConfigurationException(e);
//    }
//
//    try {
//      if (startOptions.hasIDE()) {
//        ideOptions(startOptions);
//      } else {
//        if (startOptions.hasDropDatabase()) {
//          Logger.info("Drop database on demand from the command line.");
//          Database.drop(Configuration.getInstance().getDatabaseName());
//        }
//        if (!Database.exist(Configuration.getInstance().getDatabaseName())) {
//          Database.createDatabase();
//        }
//        UserManager.getInstance().createAdministrator(startOptions);
//        User owner = UserManager.getInstance().getAdministrator();
//        createData(owner);
//      }
//    } catch (SQLException e) {
//      throw new SofiaRuntimeException("Problem configurating system in system configuration listener.", e);
//    } catch (EMailAddressAlreadyAssignedException | IOException e) {
//      throw new SofiaRuntimeException(e);
//    }
//  }
//
//  private static void ideOptions(StartOptions startOptions) throws SQLException, EMailNotExistException, EMailAddressAlreadyAssignedException, IOException, UserNotExistException, DatabaseException {
//    if ((CREATE_DATABASE || RESTORE_DATABASE) && !startOptions.hasDropDatabase() && Environment.getInstance().isDevelopment()) {
//      Database.drop(Configuration.getInstance().getDatabaseName());
//    }
//    if (!Database.exist(Configuration.getInstance().getDatabaseName())) {
//      Database.createDatabase();
//    }
//    if (CREATE_DATABASE || startOptions.hasDropDatabase()) {
//      createSites();
//
//      if (UserManager.getInstance().getAdministrator() == null) {
//        Logger.debug("Create root user for IDE enviroment.");
//        UserManager.getInstance().createSofiaAdministrator("Juan", "Cabezudo", "juan@cabezudo.net", Password.createFromPlain("1234"));
//      }
//
//      createPeople();
//
//      User owner = UserManager.getInstance().getAdministrator();
//      createClients(owner);
//      createData(owner);
//    }
//    if (RESTORE_DATABASE) {
//      Database.restoreData();
//    }
//  }
//
//  private static void createData(User owner) throws SQLException {
//    Country country = createCountries();
//    createPostalCodes(country, owner);
//  }
//
//  private static void createPeople() throws SQLException {
//    Logger.info("Create people.");
//
//    Site site = SiteManager.getInstance().getByHostame("nutricion.digital");
//    try {
//      UserManager.getInstance().set(site, "santiago@nasar.com", Password.createFromPlain("popo"));
//    } catch (EMailAddressNotExistException e) {
//      throw new SofiaRuntimeException(e);
//    }
//  }
//
//  private static void createSites() throws SQLException, IOException {
//    Logger.info("Create sites.");
//    SiteManager.getInstance().create("Manager", "localhost");
//    SiteManager.getInstance().create("Playground", "playground");
//    SiteManager.getInstance().create("Cabezudo", "cabezudo.net", "www.cabezudo.net", "local.cabezudo.net");
//    SiteManager.getInstance().create("Caballero", "caballero.mx", "local.caballero.mx");
//    SiteManager.getInstance().create("Menú electrónico", "cdmx.menu", "local.cdmx.menu");
//    SiteManager.getInstance().create("Información Condesa", "condesa.info", "local.condesa.ingo");
//    SiteManager.getInstance().create("Cornacchiola", "cornacchiola.com", "local.cornacchiola.com");
//    SiteManager.getInstance().create("Datos inútiles para impresionar en las fiestas", "datosinutilesparaimpresionarenlasfiestas.com", "local.datosinutilesparainpresionarenlasfiestas.com");
//    SiteManager.getInstance().create("Hay que comer", "hayquecomer.com", "local.hayquecomer.com");
//    SiteManager.getInstance().create("Medicina", "medicina.digital", "local.medicina.digital");
//    SiteManager.getInstance().create("Messicanissimo", "messicanisimo.it", "local.messicanisimo.it");
//    SiteManager.getInstance().create("Nutricion", "nutricion.digital", "local.nutricion.digital");
//    SiteManager.getInstance().create("Sofia", "sofia.systems", "local.sofia.systems");
//  }
//
//  private static void createClients(User owner) throws SQLException, EMailNotExistException, EMailAddressAlreadyAssignedException, UserNotExistException, DatabaseException {
//    Logger.info("Create clients");
//    String[] names = {"Juan", "José Luis", "José", "María Guadalupe", "Francisco", "Guadalupe", "María", "Juana", "Antonio", "Jesús", "Miguel Ángel", "Pedro", "Alejandro", "Manuel", "Margarita",
//      "María Del Carmen", "Juan Carlos", "Roberto", "Fernando", "Daniel", "Carlos", "Jorge", "Ricardo", "Miguel", "Eduardo", "Javier", "Rafael", "Martín", "Raúl", "David", "Josefina", "José Antonio",
//      "Arturo", "Marco Antonio", "José Manuel", "Francisco Javier", "Enrique", "Verónica", "Gerardo", "María Elena", "Leticia", "Rosa", "Mario", "Francisca", "Alfredo", "Teresa", "Alicia",
//      "María Fernanda", "Sergio", "Alberto", "Luis", "Armando", "Alejandra", "Martha", "Santiago", "Yolanda", "Patricia", "María De Los Ángeles", "Juan Manuel", "Rosa María", "Elizabeth", "Gloria",
//      "Ángel", "Gabriela", "Salvador", "Víctor Manuel", "Silvia", "María De Guadalupe", "María De Jesús", "Gabriel", "Andrés", "Óscar", "Guillermo", "Ana María", "Ramón", "María Isabel", "Pablo",
//      "Ruben", "Antonia", "María Luisa", "Luis Ángel", "María Del Rosario", "Felipe", "Jorge Jesús", "Jaime", "José Guadalupe", "Julio Cesar", "José De Jesús", "Diego", "Araceli", "Andrea", "Isabel",
//      "María Teresa", "Irma", "Carmen", "Lucía", "Adriana", "Agustín", "María De La Luz", "Gustavo"};
//    String[] lastNames = {"González", "Rodríguez", "Gómez", "Fernández", "López", "Díaz", "Martínez", "Pérez", "García", "Sánchez", "Romero", "Sosa", "Álvarez", "Torres", "Ruiz", "Ramírez", "Flores",
//      "Acosta", "Benítez", "Medina", "Suárez", "Herrera", "Aguirre", "Pereyra", "Gutiérrez", "Giménez", "Molina", "Silva", "Castro", "Rojas", "Ortíz", "Núñez", "Luna", "Juárez", "Cabrera", "Ríos",
//      "Ferreyra", "Godoy", "Morales", "Domínguez", "Moreno", "Peralta", "Vega", "Carrizo", "Quiroga", "Castillo", "Ledesma", "Muñoz", "Ojeda", "Ponce", "Vera", "Vázquez", "Villalba", "Cardozo",
//      "Navarro", "Ramos", "Arias", "Coronel", "Córdoba", "Figueroa", "Correa", "Cáceres", "Vargas", "Maldonado", "Mansilla", "Farías", "Rivero", "Paz", "Miranda", "Roldán", "Méndez", "Lucero", "Cruz",
//      "Hernández", "Agüero", "Páez", "Blanco", "Mendoza", "Barrios", "Escobar", "Ávila", "Soria", "Leiva", "Acuña", "Martin", "Maidana", "Moyano", "Campos", "Olivera", "Duarte", "Soto", "Franco",
//      "Bravo", "Valdéz", "Toledo", "Andrade", "Montenegro", "Leguizamón", "Chávez", "Arce"};
//
//    SiteManager.getInstance().getByHostame("nutricion.digital");
//    for (int i = 0; i < 357; i++) {
//      int n = random.nextInt(names.length);
//      int l = random.nextInt(lastNames.length);
//      String name = names[n];
//      String lastName = lastNames[l];
//      String address = getEMail(name, lastName);
//      Person person = PeopleManager.getInstance().getByEMailAddress(address);
//      if (person == null) {
//        Client client = ClientManager.getInstance().create(name, lastName, owner);
//        int m = random.nextInt(3);
//        for (int j = 0; j < m; j++) {
//          address = getEMail(name, lastName);
//          if (EMailManager.getInstance().get(address) == null) {
//            ClientManager.getInstance().add(client, address);
//          }
//        }
//      }
//    }
//  }
//
//  private static String getEMail(String name, String lastName) {
//    String[] eMailServers = {"gmail.com", "yahoo.com", "cabezudo.net", "mail.mx", "mailserver.com", "mymail.com", "fakemail.com", "freemail.com"};
//    int s = random.nextInt(eMailServers.length);
//    String eMailServer = eMailServers[s];
//    return name.toLowerCase() + "." + lastName.toLowerCase() + "@" + eMailServer;
//  }
//
//  private static Country createCountries() throws SQLException {
//    return CountryManager.getInstance().add("México", 52, "MX");
//  }
//
//  private static void createPostalCodes(Country country, User owner) throws SQLException {
//    Logger.info("Create postal codes.");
//    Path postalCodesPath = Configuration.getInstance().getSystemDataPath().resolve("postalCodes.csv");
//    try (BufferedReader br = new BufferedReader(new FileReader(postalCodesPath.toFile()))) {
//      String line;
//      boolean headers = true;
//      int counter = 0;
//      while ((line = br.readLine()) != null) {
//        if (CREATE_LESS_DATA) {
//          if (counter < 1000) {
//            counter++;
//            continue;
//          } else {
//            counter = 0;
//          }
//        }
//        if (headers) {
//          headers = false;
//          continue;
//        }
//        String[] fields = line.split(Pattern.quote("|"));
//
//        String stateName = fields[0];
//        String cityName = fields[1];
//        String municipalityName = fields[2];
//        String settlementName = fields[3];
//        String settlementTypeName = fields[4];
//        String zoneName = fields[5];
//        String postalCodeNumber = fields[6];
//
//        State state = StateManager.getInstance().add(country, stateName);
//
//        City city = null;
//        if (!cityName.equals("-")) {
//          city = CityManager.getInstance().add(state, cityName, owner);
//        }
//
//        try (Connection connection = Database.getConnection()) {
//
//          SettlementType settlementType = SettlementTypeManager.getInstance().add(connection, settlementTypeName);
//
//          Municipality municipality = MunicipalityManager.getInstance().add(connection, state, municipalityName, owner);
//
//          Zone zone = ZoneManager.getInstance().add(connection, zoneName);
//
//          Settlement settlement = SettlementManager.getInstance().add(connection, settlementType, city, municipality, zone, settlementName, owner);
//
//          PostalCodeManager.getInstance().add(connection, settlement, Integer.parseInt(postalCodeNumber), owner);
//        }
//      }
//    } catch (IOException e) {
//      throw new SofiaRuntimeException(e);
//    }
//  }
}
