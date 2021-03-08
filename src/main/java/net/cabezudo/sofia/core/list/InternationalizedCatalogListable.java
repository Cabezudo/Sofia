package net.cabezudo.sofia.core.list;

import java.util.List;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.catalogs.InternationalizedCatalogEntry;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.03.09
 * @param <T>
 */
public abstract class InternationalizedCatalogListable<T extends InternationalizedCatalogEntry> {

  protected int offset;

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public int getOffset() {
    return offset;
  }

  public abstract void add(T entry);

  public abstract List<T> getList();

  public JSONValue toJSONTree() {
    JSONObject listObject = new JSONObject();
    JSONArray jsonList = new JSONArray();
    JSONPair jsonListPair = new JSONPair("list", jsonList);
    listObject.add(jsonListPair);
    int row = getOffset();
    for (T entry : getList()) {
      JSONObject jsonSite = new JSONObject();
      jsonSite.add(new JSONPair("row", row));
      jsonSite.add(new JSONPair("id", entry.getId()));
      jsonSite.add(new JSONPair("name", entry.getName()));
      jsonList.add(jsonSite);
      row++;
    }

    return listObject;
  }
}
