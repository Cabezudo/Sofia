package net.cabezudo.sofia.core.users.profiles;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.16
 */
public class Profiles implements Iterable<Profile> {

  private final List<Profile> list;

  public Profiles() {
    list = new ArrayList<>();
  }

  public Profiles(Profile profile) {
    this();
    list.add(profile);
  }

  @Override
  public Iterator<Profile> iterator() {
    return list.iterator();
  }

  public void add(Profile profile) {
    if (profile == null) {
      throw new InvalidParameterException("The parameter is null");
    }
    list.add(profile);
  }

  public void add(Profiles profiles) {
    if (profiles == null) {
      throw new InvalidParameterException("The parameter is null");
    }
    for (Profile profile : profiles) {
      list.add(profile);
    }
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }
}
