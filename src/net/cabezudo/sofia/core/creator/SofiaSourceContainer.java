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

  protected abstract void apply(TemplateVariables templateVariables) throws UndefinedLiteralException;

  protected void apply(SofiaSource code, TemplateVariables templateVariables) throws UndefinedLiteralException {
    for (int i = 0; i < code.getLines().getSize(); i++) {
      Line line = code.get(i);
      line = line.replace(templateVariables);
      code.set(i, line);
    }
  }

  Line apply(Line line, TemplateVariables templateVariables) throws UndefinedLiteralException {
    return line.replace(templateVariables);
  }

  void setTargetFilePath(Path targetFilePath) {
    this.targetFilePath = targetFilePath;
  }

  Path getTargetFilePath() {
    return targetFilePath;
  }
}
