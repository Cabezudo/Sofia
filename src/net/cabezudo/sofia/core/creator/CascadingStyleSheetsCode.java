package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Set;
import static net.cabezudo.sofia.core.creator.SofiaSourceContainer.Type.CSS;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.25
 */
public class CascadingStyleSheetsCode extends SofiaSourceContainer {

  CascadingStyleSheetsCode(TemplateLiterals templateLiterals) {
    super(templateLiterals);
  }

  @Override
  public Type getType() {
    return CSS;
  }

  @Override
  public String getCode() {
    String code = super.getCode();
    StringBuilder imports = new StringBuilder();
    StringBuilder styles = new StringBuilder(code.length());

    try (Scanner scanner = new Scanner(code);) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if (line.startsWith("@import url(")) {
          imports.append(line);
          imports.append("\n");
          continue;
        }
        styles.append(line);
        styles.append("\n");
      }
    }
    StringBuilder sb = new StringBuilder(code.length());
    sb.append(imports);
    sb.append("\n");
    sb.append(styles);
    sb.append("\n");
    return sb.toString();
  }

  void load(Set<Path> fileNames) throws IOException {
    for (Path fileNamePath : fileNames) {
      String htmlFileName = fileNamePath.toString();
      String voidFileName = htmlFileName.substring(0, htmlFileName.length() - 4);
      String cssFileName = voidFileName + "css";
      Path cssFilePath = Paths.get(cssFileName);
      load(cssFilePath);
    }
  }

  @Override
  public void save(Site site, String filename) throws IOException, SiteCreationException {
    try {
      String code = templateLiterals.apply(this.getCode());
      Path jsFileSitePath = site.getCSSPath().resolve(filename + ".css");
      Files.createDirectories(jsFileSitePath.getParent());
      Logger.debug("Creating the file %s.", jsFileSitePath);
      Files.write(jsFileSitePath, code.getBytes());
    } catch (UndefinedLiteralException e) {
      throw new SiteCreationException(e.getMessage(), e, filename + ".css", e.getLine(), e.getColumn());
    }
  }
}
