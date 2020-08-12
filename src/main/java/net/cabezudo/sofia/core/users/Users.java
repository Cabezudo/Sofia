package net.cabezudo.sofia.core.users;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.07
 */
public class Users {

  private List<User> list = new ArrayList<>();

  public void add(User user) {
    list.add(user);
  }

  public int size() {
    return list.size();
  }
}
