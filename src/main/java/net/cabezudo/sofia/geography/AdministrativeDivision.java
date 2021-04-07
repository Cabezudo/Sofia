package net.cabezudo.sofia.geography;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.languages.Language;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.07
 */
public abstract class AdministrativeDivision {

  private final int id;
  private final AdministrativeDivision parent;
  private final AdministrativeDivisionName name;

  public AdministrativeDivision(int id, AdministrativeDivision parent, AdministrativeDivisionName name) {
    this.id = id;
    this.parent = parent;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public AdministrativeDivision getParent() {
    return parent;
  }

  public AdministrativeDivisionName getName() {
    return name;
  }

  public JSONObject toJSONTree() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("id", id));
    jsonObject.add(new JSONPair("parent", parent.toJSONTree()));
    jsonObject.add(new JSONPair("name", name));
    return jsonObject;
  }

  public JSONValue toWebListTree(Language language) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("name", name.get(language)));
    return jsonObject;
  }

  public String getType() {
    return this.getClass().getName().toLowerCase();
  }
}
