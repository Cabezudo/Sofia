package net.cabezudo.sofia.core.creator;

import net.cabezudo.sofia.core.configuration.Environment;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.05.04
 */
public class HTMLLines extends Lines {

  @Override
  protected Line transform(Line line) {
    if (Environment.getInstance().isProduction()) {
      if (line.isEmpty()) {
        return null;
      }
    }
    return line;
  }
}
