package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
abstract class SofiaSourceContainer {

  private Path targetFilePath;

  abstract void save() throws SiteCreationException, IOException;

  protected abstract void apply(TemplateLiterals templateLiterals) throws UndefinedLiteralException;

  protected void apply(SofiaSource code, TemplateLiterals templateLiterals) throws UndefinedLiteralException {
    for (int i = 0; i < code.getLines().getSize(); i++) {
      Line line = code.get(i);
      line = line.replace(templateLiterals);
      code.set(i, line);
    }
  }

  Line apply(Line line, TemplateLiterals templateLiterals) throws UndefinedLiteralException {
    return line.replace(templateLiterals);
  }

  void setTargetFilePath(Path targetFilePath) {
    this.targetFilePath = targetFilePath;
  }

  Path getTargetFilePath() {
    return targetFilePath;
  }
}
