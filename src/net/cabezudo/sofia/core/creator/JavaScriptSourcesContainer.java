package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
class JavaScriptSourcesContainer extends SofiaSourceContainer {

  private final List<SofiaJavaScriptSource> files;

  JavaScriptSourcesContainer() {
    this.files = new ArrayList<>();
  }

  @Override
  void save() throws SiteCreationException, IOException {
    Logger.debug("Saving file %s.", getTargetFilePath());
    StringBuilder code = new StringBuilder();
    for (SofiaSource sourceFile : files) {
      String description = sourceFile.getDescription();
      if (description != null) {
        code.append(description);
      }
      code.append(sourceFile.getCode());
      code.append("\n");
    }
    Files.write(getTargetFilePath(), code.toString().getBytes(Configuration.getInstance().getEncoding()));
  }

  void add(Libraries libraries) throws IOException {
    for (Library library : libraries) {
      List<SofiaJavaScriptSource> sources = library.getJavaScritpPaths();
      for (SofiaJavaScriptSource sofiaSource : sources) {
        files.add(sofiaSource);
      }
    }
  }

  void add(SofiaJavaScriptSource sofiaSource) {
    files.add(sofiaSource);
  }

  @Override
  protected void apply(TemplateLiterals templateLiterals) throws UndefinedLiteralException {
    for (SofiaJavaScriptSource file : files) {
      super.apply(file, templateLiterals);
    }
  }
}
