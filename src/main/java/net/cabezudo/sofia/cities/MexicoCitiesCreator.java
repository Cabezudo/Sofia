package net.cabezudo.sofia.cities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Pattern;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.configuration.ConfigurationException;
import net.cabezudo.sofia.core.data.DataCreator;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.exceptions.DataConversionException;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.geolocation.Latitude;
import net.cabezudo.sofia.core.geolocation.Longitude;
import net.cabezudo.sofia.core.languages.InvalidTwoLettersCodeException;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.languages.LanguageManager;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.countries.Country;
import net.cabezudo.sofia.countries.CountryManager;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.states.State;
import net.cabezudo.sofia.states.StateManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.05
 */
public class MexicoCitiesCreator implements DataCreator {

  // 00:33:11.022
  @Override
  public void create() throws ClusterException, ConfigurationException, DataConversionException {
    User owner = UserManager.getInstance().getAdministrator();
    Language language;
    try {
      language = LanguageManager.getInstance().get("es");
    } catch (InvalidTwoLettersCodeException e) {
      throw new SofiaRuntimeException(e);
    }
    Country country = CountryManager.getInstance().get("MX");
    Path citiesFilePath = CityManager.getInstance().getDataFile(country.getTwoLetterCountryCode());

    Logger.info("Create citites for country %s using file: %s", country.getTwoLetterCountryCode(), citiesFilePath);
    if (!Files.exists(citiesFilePath)) {
      Logger.info("The cities file doesn't exists.");
    }
    String line;
    int lineNumber = 0;
    try (BufferedReader br = new BufferedReader(new FileReader(citiesFilePath.toFile())); Connection connection = Database.getConnection()) {
      long startTime = new Date().getTime();
      while ((line = br.readLine()) != null) {
        lineNumber++;
        if (line.isBlank()) {
          continue;
        }
        String[] fields = line.split(Pattern.quote("|"));
        String stateName = fields[0];
        State state = StateManager.getInstance().get(connection, country, stateName);
        if (state == null) {
          throw new DataConversionException("Can't find the state '" + stateName + "' for '" + country.getTwoLetterCountryCode() + "'. Line: " + lineNumber);
        }
        String cityName = fields[1];
        Latitude latitude = new Latitude(fields[2]);
        Longitude longitude = new Longitude(fields[3]);
        CityManager.getInstance().add(state, cityName, latitude, longitude, owner);
      }
      long endTime = new Date().getTime();
      Logger.debug("Time: %s", Utils.getMillisecondsToTime(endTime - startTime));
    } catch (SQLException | ClusterException e) {
      throw new ClusterException("Line: " + lineNumber, e);
    } catch (IOException e) {
      throw new ConfigurationException("Can not read the file with the cities: " + citiesFilePath);
    }
  }
}
