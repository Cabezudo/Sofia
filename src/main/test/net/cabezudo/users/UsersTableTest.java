package net.cabezudo.users;

import net.cabezudo.sofia.core.users.UsersTable;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.24
 */
public class UsersTableTest {

  @Test
  public void testConstructor() {
    Assert.assertNotNull(new UsersTable());
  }

}
