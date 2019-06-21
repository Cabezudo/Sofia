package net.cabezudo.ws.parser;

import net.cabezudo.sofia.core.ws.parser.URLPathTokenizer;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.05
 */
public class URLPathTokenizerTest {

  @Test
  public void testTokenize() {
    Assert.assertNotNull(new URLPathTokenizer());
  }

}
