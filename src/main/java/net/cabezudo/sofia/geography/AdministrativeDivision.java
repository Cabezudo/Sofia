package net.cabezudo.sofia.geography;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.languages.InvalidTwoLettersCodeException;
import net.cabezudo.sofia.core.languages.Language;
import net.cabezudo.sofia.names.InternationalizedName;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.04.07
 */
public class AdministrativeDivision {

  private final int id;
  private final AdministrativeDivisionType type;
  private final String code;
  private final Integer fileId;
  private final int parentId;
  private final AdministrativeDivision parent;

  public AdministrativeDivision(int id, AdministrativeDivisionType type, String code, Integer fileId, AdministrativeDivision parent) {
    this.id = id;
    this.type = type;
    this.code = code;
    this.fileId = fileId;
    this.parentId = parent == null ? 0 : parent.getId();
    this.parent = parent;
  }

  public AdministrativeDivision(int id, AdministrativeDivisionType type, String code, Integer fileId, int parentId) {
    this.id = id;
    this.type = type;
    this.code = code;
    this.fileId = fileId;
    this.parentId = parentId;
    this.parent = null;
  }

  @Override
  public String toString() {
    return "[ id = " + id + ", type = " + type + ", code = " + code + ", fieldId = " + fileId + ", parentId = " + parentId + " ]";
  }

  public int getId() {
    return id;
  }

  public AdministrativeDivisionType getType() {
    return type;
  }

  public String getCode() {
    return code;
  }

  public int getFileId() {
    return fileId;
  }

  public AdministrativeDivision getParent() {
    return parent;
  }

  public JSONObject toJSONTree() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("type", id));
    jsonObject.add(new JSONPair("type", type.getName()));
    jsonObject.add(new JSONPair("code", code));
    jsonObject.add(new JSONPair("parent", parent));
    return jsonObject;
  }

  public JSONValue toWebListTree(Language language) throws ClusterException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("code", code));
    jsonObject.add(new JSONPair("name", getName(language)));
    return jsonObject;
  }

  public InternationalizedName getName(Language language) throws ClusterException {
    InternationalizedName name;
    try {
      name = AdministrativeDivisionNameManager.getInstance().get(id, language);
    } catch (InvalidTwoLettersCodeException e) {
      throw new SofiaRuntimeException(e);
    }
    if (name == null) {
      return null;
    }
    return name;
  }

}
