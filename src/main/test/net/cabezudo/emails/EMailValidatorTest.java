package net.cabezudo.emails;

import net.cabezudo.sofia.emails.EMailValidator;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.14
 */
public class EMailValidatorTest {

  @Test
  public void testValidate() throws Exception {
    Assert.assertNotNull(new EMailValidator());
  }
}
