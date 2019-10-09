package net.cabezudo.domains;

import net.cabezudo.sofia.core.sites.domainname.DomainNameValidator;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.06
 */
public class DomainValidatorTest {

  @Test
  public void testValidate() throws Exception {
    Assert.assertNotNull(new DomainNameValidator());
  }
}
