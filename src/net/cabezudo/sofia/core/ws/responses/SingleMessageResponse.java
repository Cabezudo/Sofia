package net.cabezudo.sofia.core.ws.responses;

import java.util.Locale;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.04
 */
public class SingleMessageResponse extends AbstractResponse {

  private final AbstractMessage message;

  public SingleMessageResponse(AbstractMessage message) {
    this.message = message;
  }

  @Override
  public String toJSON(Site site, Locale locale) {
    return message.toJSON(site, locale).toString();
  }
}
