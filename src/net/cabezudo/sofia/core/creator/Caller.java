package net.cabezudo.sofia.core.creator;

import java.nio.file.Path;
import net.cabezudo.sofia.core.configuration.Configuration;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
class Caller {

  private final Path relativePath;
  private final Path filePath;
  private final Integer lineNumber;

  Caller(Path basePath, Path relativePath, Integer lineNumer) {
    this.relativePath = relativePath;
    this.filePath = basePath.resolve(relativePath);
    this.lineNumber = lineNumer;
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
    Path relativizedFilePath = Configuration.getInstance().getCommonSourcesPath().relativize(filePath);
    return relativizedFilePath + ":" + getLineNumber();
  }
}
