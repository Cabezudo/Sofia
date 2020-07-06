package net.cabezudo.sofia.core.sic.objects.values;

import java.nio.file.Files;
import java.nio.file.Path;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICImageFilePath extends SICValue<Path> {

  private Path filePath;

  public SICImageFilePath(Path basePath, Token token) throws SICCompileTimeException {
    super(token);
    String imageFileName = getToken().getValue();
    if (basePath == null) {
      throw new RuntimeException("The image base path IS NOT defined.");
    }
    String newImageFileName = imageFileName; // Just for show the old path in the error.
    while (newImageFileName.startsWith("/")) {
      Logger.warning("imageFileName start with slash character");
      newImageFileName = newImageFileName.substring(1);
    }
    filePath = basePath.resolve(newImageFileName);
    Logger.info("File to serarch: %s.", filePath);
    if (!Files.exists(filePath)) {
      throw new SICCompileTimeException("The file " + imageFileName + " do NOT exist.", getToken());
    }
  }

  @Override
  public String getTypeName() {
    return "imagePath";
  }

  @Override
  public boolean isImageFilePath() {
    return true;
  }

  @Override
  public Path getValue() {
    return filePath;
  }
}
