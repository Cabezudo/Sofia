package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.25
 */
abstract class AbstractSofiaSourceFile {

  private final Path basePath;
  private final Path partialPath;
  private final TemplateVariables templateVariables;
  private final Caller caller;

  AbstractSofiaSourceFile(Path basePath, Path partialPath, TemplateVariables templateVariables, Caller caller) throws IOException {
    this.basePath = basePath;
    this.partialPath = partialPath;
    this.templateVariables = templateVariables;
    this.caller = caller;
  }

  abstract void add(String l, int lineNumber);

  Path getFullTargetFilePath() {
    return basePath.resolve(partialPath);
  }

  Path getPartialPath() {
    return partialPath;
  }
}
