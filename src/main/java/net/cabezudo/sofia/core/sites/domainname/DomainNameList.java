package net.cabezudo.sofia.core.sites.domainname;

import java.util.Iterator;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.list.EntryList;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.01.28
 */
public class DomainNameList extends EntryList<DomainName> {

  private final DomainNames domainNames = new DomainNames();

  public DomainNameList(int offset) {
    super(offset);
  }

  public DomainNameList() {
    this(0);
  }

  public DomainName[] toArray() {
    return domainNames.toArray();
  }

  public void add(DomainName domainName) {
    domainNames.add(domainName);
  }

  @Override
  public Iterator<DomainName> iterator() {
    return domainNames.iterator();
  }

  @Override
  public String toJSON() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (DomainName domainName : domainNames) {
      sb.append(domainName.toJSON());
      sb.append(", ");
    }
    if (!domainNames.isEmpty()) {
      sb = Utils.chop(sb, 2);
    }
    sb.append("]");
    return sb.toString();
  }

  public void add(DomainNameList domainNames) {
    for (DomainName domainName : domainNames) {
      add(domainName);
    }
  }

  @Override
  public JSONValue toJSONTree() {
    JSONObject listObject = new JSONObject();
    JSONArray jsonRecords = new JSONArray();
    JSONPair jsonRecordsPair = new JSONPair("records", jsonRecords);
    listObject.add(jsonRecordsPair);
    domainNames.forEach((domainName) -> {
      JSONObject jsonPerson = new JSONObject();
      jsonPerson.add(new JSONPair("id", domainName.getId()));
      jsonPerson.add(new JSONPair("name", domainName.getName()));
      jsonRecords.add(jsonPerson);
    });

    return listObject;
  }

  public int size() {
    return domainNames.size();
  }

  public boolean isEmpty() {
    return domainNames.isEmpty();
  }

  @Override
  public void toFormatedString(StringBuilder sb, int indent, boolean includeFirst) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
