package net.cabezudo.sofia.core.creator;

import java.nio.file.Path;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
class Caller {

  private final Path relativePath;
  private final Path filePath;
  private final Integer lineNumber;
  private final Caller caller;

  Caller(Path basePath, Path relativePath, Integer lineNumer, Caller caller) {
    this.relativePath = relativePath;
    this.filePath = basePath.resolve(relativePath);
    this.lineNumber = lineNumer;
    this.caller = caller;
  }

  public Path getRelativePath() {
    return relativePath;
  }

  public Path getFilePath() {
    return filePath;
  }

  public Integer getLineNumber() {
    return lineNumber;
  }

  @Override
  public String toString() {
    if (caller == null) {
      return relativePath + ":" + getLineNumber();
    } else {
      return relativePath + ":" + getLineNumber() + " called from " + caller;
    }
  }
}
