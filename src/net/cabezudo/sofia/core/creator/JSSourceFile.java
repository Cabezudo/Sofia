package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.12.03
 */
class JSSourceFile extends SofiaSourceFile {

  private final Lines lines;

  JSSourceFile(Site site, Path basePath, Path partialPath, TemplateVariables templateVariables, Caller caller) {
    super(site, basePath, partialPath, templateVariables, caller);
    this.lines = new Lines();
  }

  Lines getLines() {
    return lines;
  }

  @Override
  public void add(Line line) {
    lines.add(line);
  }

  void add(Libraries libraries) {
    for (Library library : libraries) {
      for (JSSourceFile file : library.getJSFiles()) {
        Logger.debug("Reading file %s.", file.getPartialPath());
        Lines linesFromFile = file.getLines();
        for (Line line : linesFromFile) {
          add(line);
        }
      }
    }
  }

  void save(Path filePath) throws IOException {
    Logger.debug("Creating the js file %s.", filePath);
    StringBuilder code = new StringBuilder();
    for (Line line : lines) {
      code.append(line).append('\n');
    }
    Files.write(filePath, code.toString().getBytes(Configuration.getInstance().getEncoding()));
  }

  @Override
  public String getVoidPartialPathName() {
    String partialPathName = getPartialPath().toString();
    return partialPathName.substring(0, partialPathName.length() - 3);
  }
}
