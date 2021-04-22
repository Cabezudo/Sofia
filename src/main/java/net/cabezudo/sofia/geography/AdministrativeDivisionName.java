package net.cabezudo.sofia.geography;

import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.names.InternationalizedName;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.07
 */
public class AdministrativeDivisionName extends InternationalizedName {

  public AdministrativeDivisionName(int id, Language language, String value) {
    super(id, language, value);
  }

  public AdministrativeDivisionName(InternationalizedName name) {
    super(name);
  }

}
