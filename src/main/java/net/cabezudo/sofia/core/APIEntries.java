package net.cabezudo.sofia.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.10.19
 */
public class APIEntries implements Iterable<APIEntry> {

  private final List<APIEntry> list = new ArrayList<>();

  public void add(JSONArray array) {
    for (JSONValue jsonValue : array) {
      JSONObject jsonEntry = jsonValue.toJSONObject();
      APIEntry apiEntry = new APIEntry(jsonEntry.getNullString("path"), jsonEntry.getNullString("class"));
      list.add(apiEntry);
    }
  }

  @Override
  public Iterator<APIEntry> iterator() {
    return list.iterator();
  }
}
