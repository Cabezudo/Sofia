package net.cabezudo.sofia.countries;

import java.sql.Connection;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.core.words.Word;
import net.cabezudo.sofia.core.words.WordManager;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.11.23
 */
public class ContryNamesManager extends WordManager<CountryName> {

  private static final ContryNamesManager INSTANCE = new ContryNamesManager();

  private ContryNamesManager() {
    super(CountryNamesTable.DATABASE, CountryNamesTable.NAME);
  }

  public static ContryNamesManager getInstance() {
    return INSTANCE;
  }

  @Override
  public CountryName get(Connection connection, Language language, String value) throws ClusterException {
    Word word = super.get(connection, language, value);
    if (word == null) {
      return null;
    }
    return new CountryName(word);
  }

  @Override
  public CountryName add(Connection connection, int targetId, Language language, String value) throws ClusterException {
    Word word = super.add(connection, targetId, language, value);
    return new CountryName(word);
  }
}
