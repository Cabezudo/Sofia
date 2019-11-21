package net.cabezudo.sofia.core.creator;

import java.nio.file.Path;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.06.06
 */
public class UndefinedLiteralException extends Exception {

  private final String literal;
  private final Path filePath;
  private final Position position;

  UndefinedLiteralException(String literal, Path filePath, Position position, Throwable cause) {
    super("Undefined literal: " + literal, cause);
    this.literal = literal;
    this.filePath = filePath;
    this.position = position;
  }

  Position getPosition() {
    return position;
  }

  Path getFilePath() {
    return filePath;
  }

  String getUndefinedLiteral() {
    return literal;
  }
}
