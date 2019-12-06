package net.cabezudo.sofia.core.creator;

import java.nio.file.Path;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.12.03
 */
class CSSSourceFile extends SofiaSourceFile {

  private final Lines lines;

  CSSSourceFile(Site site, Path basePath, Path partialPath, TemplateVariables templateVariables, Caller caller) {
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

  @Override
  public String getVoidPartialPathName() {
    String partialPathName = getPartialPath().toString();
    return partialPathName.substring(0, partialPathName.length() - 4);
  }
}
