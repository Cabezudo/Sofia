package net.cabezudo.sofia.core.users.permission;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.16
 */
public class Permissions implements Iterable<Permission> {

  private final List<Permission> list;

  public Permissions() {
    list = new ArrayList<>();
  }

  public Permissions(Permission permission) {
    this();
    list.add(permission);
  }

  @Override
  public Iterator<Permission> iterator() {
    return list.iterator();
  }

  public void add(Permission permission) {
    if (permission == null) {
      throw new InvalidParameterException("The parameter is null");
    }
    list.add(permission);
  }

}
