package net.cabezudo.sofia.core.creator;

import net.cabezudo.sofia.core.configuration.Environment;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.05.04
 */
public class JSLines extends Lines {

  @Override
  protected boolean filter(Line line) {
    if (Environment.getInstance().isProduction()) {
      if (line.startWith("console.log")) {
        return true;
      }
      if (line.startWith("// ")) {
        return true;
      }
      return (line.startWith("console.trace"));
    }
    return false;
  }
}
