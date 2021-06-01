package net.cabezudo.sofia.core.creator;

import java.nio.file.Path;
import net.cabezudo.sofia.core.Position;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.06.15
 */
public class InvalidFragmentTag extends LocatedSiteCreationException {

  private static final long serialVersionUID = 1L;

  InvalidFragmentTag(String message, Path filePath, Position position) {
    super(message, filePath, position);
  }
}
