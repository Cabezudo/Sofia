package net.cabezudo.sofia.states;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.configuration.ConfigurationException;
import net.cabezudo.sofia.core.data.DataCreator;
import net.cabezudo.sofia.core.database.sql.Database;
import net.cabezudo.sofia.core.exceptions.DataConversionException;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.languages.InvalidTwoLettersCodeException;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.languages.LanguageManager;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.countries.Country;
import net.cabezudo.sofia.countries.CountryManager;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.05
 */
public class MexicoStatesCreator implements DataCreator {

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
    Path statesFilePath = StateManager.getInstance().getDataFile(country.getTwoLetterCountryCode());

    Logger.info("Create states for country %s using file: %s", country.getTwoLetterCountryCode(), statesFilePath);
    if (!Files.exists(statesFilePath)) {
      Logger.info("The states file doesn't exists.");
    }
    String stateName;
    int counter = 0;
    try (BufferedReader br = new BufferedReader(new FileReader(statesFilePath.toFile())); Connection connection = Database.getConnection()) {
      long startTime = new Date().getTime();
      while ((stateName = br.readLine()) != null) {
        counter++;
        if (!stateName.isBlank()) {
          StateManager.getInstance().add(country, stateName);
        }
      }
      long endTime = new Date().getTime();
      Logger.debug("Time: %s", Utils.getMillisecondsToTime(endTime - startTime));
    } catch (SQLException | ClusterException e) {
      throw new ClusterException("Line: " + counter, e);
    } catch (IOException e) {
      throw new ConfigurationException("Can not read the file with the states: " + statesFilePath);
    }
  }
}
