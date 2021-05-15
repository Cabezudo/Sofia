package net.cabezudo.sofia.core.creator;

import java.nio.file.Path;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
public class Caller {

  private final Path basePath;
  private final Path relativePath;
  private final Integer lineNumber;
  private final Caller caller;

  private Caller(Path basePath, Path relativePath, Integer lineNumer) {
    this.basePath = basePath;
    this.relativePath = relativePath;
    this.lineNumber = lineNumer;
    this.caller = null;
  }

  Caller(Path basePath, Path relativePath, Integer lineNumer, Caller caller) {
    this.basePath = basePath;
    this.relativePath = relativePath;
    this.lineNumber = lineNumer;
    this.caller = caller;
  }

  Caller(HTMLSourceFile sourceFile, int lineNumber) {
    this.basePath = sourceFile.getBasePath();
    this.relativePath = sourceFile.getPartialFilePath();
    this.lineNumber = lineNumber;
    this.caller = sourceFile.getCaller();
  }

  public Path getRelativePath() {
    return relativePath;
  }

  public Path getBasePath() {
    return basePath;
  }

  public Path getFullPath() {
    return basePath.resolve(relativePath);
  }

  public Integer getLineNumber() {
    return lineNumber;
  }

  public Caller getCaller() {
    return caller;
  }

  @Override
  public String toString() {
    if (caller == null) {
      if (getLineNumber() == 0) {
        return relativePath.toString();
      } else {
        return relativePath + ":" + getLineNumber();
      }
    } else {
      if (getLineNumber() == 0) {
        return relativePath + " called from " + caller;
      } else {
        return relativePath + ":" + getLineNumber() + " called from " + caller;
      }
    }
  }
}
