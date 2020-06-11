package net.cabezudo.sofia.core.creator;

import java.nio.file.Path;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.16
 */
public class LocatedSiteCreationException extends SiteCreationException {

  private static final long serialVersionUID = 1L;

  private final Path filePath;
  private final Position position;

  LocatedSiteCreationException(String message, Path filePath, Position position) {
    super(message + " at " + filePath + ":" + position);
    this.filePath = filePath;
    this.position = position;
  }

  public Path getFilePath() {
    return filePath;
  }

  public Position getPosition() {
    return position;
  }
}
