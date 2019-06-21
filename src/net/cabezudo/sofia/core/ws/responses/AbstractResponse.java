package net.cabezudo.sofia.core.ws.responses;

import java.util.Locale;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.10
 */
public abstract class AbstractResponse {

  public abstract String toJSON(Site sites, Locale locale);
}
