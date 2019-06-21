package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import net.cabezudo.sofia.core.configuration.Environment;
import static net.cabezudo.sofia.core.creator.SofiaSourceContainer.Type.JS;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.25
 */
public class JavaScriptCode extends SofiaSourceContainer {

  JavaScriptCode(TemplateLiterals templateLiterals) {
    super(templateLiterals);
  }

  @Override
  public Type getType() {
    return JS;
  }

  void load(Set<Path> fileNames) throws IOException {
    for (Path fileNamePath : fileNames) {
      String htmlFileName = fileNamePath.toString();
      String voidFileName = htmlFileName.substring(0, htmlFileName.length() - 4);
      String jsFileName = voidFileName + "js";
      Path jsFilePath = Paths.get(jsFileName);
      load(jsFilePath);
    }
  }

  @Override
  protected void processLine(String line) {
    if (!Environment.getInstance().isLocal() && line.trim().startsWith("/*") && line.trim().endsWith("*/")) {
      return;
    }
    super.processLine(line);
  }

  @Override
  public void save(Site site, String fileName) throws IOException, SiteCreationException {
    try {
      String code = templateLiterals.apply(this.getCode());
      Path jsFileSitePath = site.getJSPath().resolve(fileName + ".js");
      Files.createDirectories(jsFileSitePath.getParent());
      Logger.debug("Creating the file %s.", jsFileSitePath);
      Files.write(jsFileSitePath, code.getBytes());
    } catch (UndefinedLiteralException e) {
      throw new SiteCreationException(e);
    }
  }
}
