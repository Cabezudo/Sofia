package net.cabezudo.sofia.core.creator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import net.cabezudo.sofia.core.creator.SofiaSourceContainer.Type;
import static net.cabezudo.sofia.core.creator.SofiaSourceContainer.Type.HTML;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.02.19
 */
public class HTMLSource extends SofiaSourceContainer {

  HTMLSource(TemplateLiterals templateLiterals) {
    super(templateLiterals);
  }

  @Override
  public String getCode() {
    return sb.toString();
  }

  @Override
  public Type getType() {
    return HTML;
  }

  @Override
  void load(Path filePath) throws IOException {
    if (Files.exists(filePath)) {
      try (Stream<String> stream = Files.lines(filePath)) {
        stream.forEach(this::processLine);
      }
    }
  }

  @Override
  protected void processLine(String line) {
    append(line);
    append("\n");
  }

  @Override
  public void save(Site site, String fileName) throws IOException, SiteCreationException {
    try {
      String code = templateLiterals.apply(this.getCode());
      Path jsFileSitePath = site.getVersionPath().resolve(fileName + ".html");
      Files.createDirectories(jsFileSitePath.getParent());
      Logger.debug("Creating the file %s.", jsFileSitePath);
      Files.write(jsFileSitePath, code.getBytes(StandardCharsets.UTF_8));
    } catch (UndefinedLiteralException e) {
      throw new SiteCreationException(e);
    }
  }
}
