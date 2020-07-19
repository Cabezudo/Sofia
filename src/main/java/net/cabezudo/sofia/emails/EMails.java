package net.cabezudo.sofia.emails;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.13
 */
public class EMails implements Iterable<EMail> {

  private final Set<EMail> set = new TreeSet<>();
  private final Map<Integer, EMail> hash = new TreeMap<>();
  private EMail primaryEMail;

  @Override
  public Iterator<EMail> iterator() {
    return set.iterator();
  }

  public EMails() {
  }

  public EMails(EMail eMail) {
    add(eMail);
  }

  public EMails(EMails eMails) {
    for (EMail eMail : eMails) {
      add(eMail);
    }
    this.primaryEMail = eMails.primaryEMail;
  }

  public final void add(EMail eMail) {
    set.add(eMail);
    hash.put(eMail.getId(), eMail);
  }

  public final void add(EMails eMails) {
    for (EMail eMail : eMails) {
      add(eMail);
    }
  }

  public void setPrimaryEMailById(Integer primaryEMailId) {
    this.primaryEMail = hash.get(primaryEMailId);
  }

  public EMail getPrimaryEMail() {
    return primaryEMail;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("[ primaryEMail = " + primaryEMail + ", ");
    for (EMail email : set) {
      sb.append(email);
      sb.append(", ");
    }
    sb.append(" ]");
    return sb.toString();
  }

  public String toJSON() {
    return toJSONTree().toJSON();
  }

  public JSONValue toJSONTree() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.add(new JSONPair("primaryEMail", primaryEMail == null ? null : primaryEMail.toJSON()));
    JSONArray jsonEMails = new JSONArray();
    jsonObject.add(new JSONPair("list", jsonEMails));
    for (EMail eMail : set) {
      jsonEMails.add(eMail.toJSON());
    }
    return jsonObject;
  }
}
