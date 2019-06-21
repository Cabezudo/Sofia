package net.cabezudo.sofia.core.ws.responses;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.sofia.core.sites.Site;
import static net.cabezudo.sofia.core.ws.responses.AbstractMessage.Type.ERROR;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.04
 */
public final class Messages implements Iterable<AbstractMessage> {

  private final List<AbstractMessage> list = new ArrayList<>();
  private boolean hasErrors;

  public void add(AbstractMessage message) {
    list.add(message);
    if (message.getType() == ERROR) {
      hasErrors = true;
    }
  }

  public void add(Messages messages) {
    for (AbstractMessage message : messages) {
      add(message);
    }
  }

  @Override
  public Iterator<AbstractMessage> iterator() {
    return list.iterator();
  }

  public JSONArray toJSON(Site site, Locale locale) {
    JSONArray jsonArray = new JSONArray();
    for (AbstractMessage abstractMessage : list) {
      jsonArray.add(abstractMessage.toJSON(site, locale));
    }
    return jsonArray;
  }

  public boolean hasErrors() {
    return hasErrors;
  }
}
