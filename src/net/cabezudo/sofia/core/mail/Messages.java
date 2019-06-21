package net.cabezudo.sofia.core.mail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.cabezudo.json.values.JSONArray;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.11
 */
public class Messages implements Iterable<Message> {

  List<Message> list = new ArrayList<>();

  public Messages(Message message) {
    list.add(message);
  }

  JSONArray toJSON() {
    JSONArray jsonList = new JSONArray();
    list.forEach((message) -> {
      jsonList.add(message.toJSONTree());
    });
    return jsonList;
  }

  @Override
  public Iterator<Message> iterator() {
    return list.iterator();
  }
}
