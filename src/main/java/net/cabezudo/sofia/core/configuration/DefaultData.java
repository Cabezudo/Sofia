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
import java.util.regex.Pattern;
import net.cabezudo.sofia.cities.City;
import net.cabezudo.sofia.cities.CityManager;
import net.cabezudo.sofia.core.StartOptions;
import net.cabezudo.sofia.core.database.Database;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.sites.domainname.DomainNameManager;
import net.cabezudo.sofia.core.sites.domainname.DomainNameMaxSizeException;
import net.cabezudo.sofia.core.sites.domainname.DomainNameNotExistsException;
import net.cabezudo.sofia.core.sites.domainname.EmptyDomainNameException;
import net.cabezudo.sofia.core.sites.domainname.InvalidCharacterException;
import net.cabezudo.sofia.core.sites.domainname.MissingDotException;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.core.users.UserNotExistException;
import net.cabezudo.sofia.countries.Country;
import net.cabezudo.sofia.countries.CountryManager;
import net.cabezudo.sofia.emails.EMailNotExistException;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.municipalities.Municipality;
import net.cabezudo.sofia.municipalities.MunicipalityManager;
import net.cabezudo.sofia.postalcodes.PostalCodeManager;
import net.cabezudo.sofia.settlements.Settlement;
import net.cabezudo.sofia.settlements.SettlementManager;
import net.cabezudo.sofia.settlements.SettlementType;
import net.cabezudo.sofia.settlements.SettlementTypeManager;
import net.cabezudo.sofia.states.State;
import net.cabezudo.sofia.states.StateManager;
import net.cabezudo.sofia.zones.Zone;
import net.cabezudo.sofia.zones.ZoneManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.26
 */
public class DefaultData {

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
        Database.create();
        createSites();
        UserManager.getInstance().createAdministrator();
        User owner = UserManager.getInstance().getAdministrator();
        createData(owner);
      }
    } catch (SQLException | IOException e) {
      throw new RuntimeConfigurationException("Error in system configuration listener.", e);
    }
  }

  private static void createData(User owner) throws SQLException {
    Country country = createCountries();
    createPostalCodes(country, owner);
  }

  private static void createSites() throws SQLException, IOException {
    Logger.info("Create sites.");
    Site site;
    if (System.console() != null) {
      String baseDomainName;
      System.out.println("Crear sitio para el servidor.");
      System.out.println(
              "Coloque el nombre del host con el cual desea administrar el sitio en la red. Debe ser un nombre de dominio válido. "
              + "Si solo va a trabajar de forma local puede dejarlo en blanco y utilizar localhost para acceder a la configuración. "
              + "Pero si necesita acceder al sitio de forma remota debera colocar un nombre de dominio válido");
      System.out.print("Dominio base: ");
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
          System.out.println("The domain name is empty.");
        } catch (InvalidCharacterException e) {
          System.out.println("Invalid character '" + e.getChar() + "' in domain name");
        } catch (DomainNameNotExistsException e) {
          System.out.println("The domain name doesn't exist. Don't hava a DNS entry.");
        } catch (DomainNameMaxSizeException e) {
          System.out.println("The domain name is too large.");
        } catch (MissingDotException e) {
          System.out.println("A domain name must have a dot in it.");
        }
      } while (!validDomain);
      site = SiteManager.getInstance().create("Manager", "manager", "localhost", baseDomainName);
    } else {
      site = SiteManager.getInstance().create("Manager", "manager", "localhost");
    }

    SiteManager.getInstance().create("Playground", "playground");
  }

  private static Country createCountries() throws SQLException {
    return CountryManager.getInstance().add("México", 52, "MX");
  }

  private static void createPostalCodes(Country country, User owner) throws SQLException {
    Logger.info("Create postal codes.");
    Path postalCodesPath = Configuration.getInstance().getSystemDataPath().resolve("postalCodes.csv");
    if (Files.exists(postalCodesPath)) {
      try (BufferedReader br = new BufferedReader(new FileReader(postalCodesPath.toFile()))) {
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

          try (Connection connection = Database.getConnection(Configuration.getInstance().getDatabaseName())) {
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
}
