package net.cabezudo.sofia.core.sic;

import java.util.ArrayList;
import java.util.List;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.sofia.core.sic.tokens.Position;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.25
 */
public class SICCompilerMessages {

  private final List<SICCompilerMessage> messages = new ArrayList<>();

  public JSONArray toJSON() {
    JSONArray jsonMessages = new JSONArray();
    messages.forEach((message) -> {
      jsonMessages.add(message.toJSON());
    });
    return jsonMessages;
  }

  public void add(String message, Position position) {
    System.out.println(message);
    SICCompilerMessage sicComplierMessage = new SICCompilerMessage(message, position);
    messages.add(sicComplierMessage);
  }

}
