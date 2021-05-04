package net.cabezudo.sofia.core.creator;

import net.cabezudo.sofia.core.configuration.Environment;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.05.04
 */
public class JSLines extends Lines {

  @Override
  protected Line transform(Line line) {
    if (Environment.getInstance().isProduction()) {
      if (line.isEmpty()) {
        return null;
      }
      if (line.startWith("console.warn")) {
        return null;
      }
      if (line.startWith("console.log")) {
        return null;
      }
      if (line.startWith("//")) {
        return null;
      }
      if (line.startWith("/*") && line.endWith("*/")) {
        return null;
      }
      if (line.startWith("console.trace")) {
        return null;
      }
    }
    return line;
  }
}
