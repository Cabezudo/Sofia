package net.cabezudo.sofia.core;

import java.nio.file.Path;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
public class PositionFile {

  private final Path path;
  private final Position position;

  public PositionFile(Path path, Position position) {
    this.path = path;
    this.position = position;
  }

  PositionFile(Path path, int line, int row) {
    this.path = path;
    this.position = new Position(line, row);
  }

  @Override
  public String toString() {
    return path + ":" + position;
  }
}
