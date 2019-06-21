package net.cabezudo.password;

import net.cabezudo.sofia.core.passwords.Password;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.24
 */
public class PasswordTest {

  @Test
  public void testToString() {
    Password password = Password.createFromPlain("popo");
    String string = password.toString();
    Assert.assertEquals("popo : cG9wbw==", string);
  }
}
