package net.cabezudo.logger;

import net.cabezudo.sofia.core.logger.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.05
 */
public class LoggerTest {

  @Test
  public void testLoggerConstructor() {
    Logger logger = new Logger();
    Assert.assertNotNull(logger);
  }
}
