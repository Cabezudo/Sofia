package net.cabezudo.sofia.geography;

import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.words.MultiLanguageWord;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.07
 */
public class AdministrativeDivisionName extends MultiLanguageWord {

  public AdministrativeDivisionName(JSONObject jsonObject) throws JSONParseException, ClusterException {
    super(jsonObject);
  }

}
