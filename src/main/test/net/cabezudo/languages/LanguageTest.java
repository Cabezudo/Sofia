package net.cabezudo.languages;

import net.cabezudo.sofia.languages.Language;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.24
 */
public class LanguageTest {

  @Test
  public void testConstructor() {
    Assert.assertNotNull(new Language());
  }

}
