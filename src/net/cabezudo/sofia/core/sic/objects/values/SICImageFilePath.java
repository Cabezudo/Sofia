package net.cabezudo.sofia.core.sic.objects.values;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
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
    if (imageFileName.startsWith(File.separator)) {
      throw new SICCompileTimeException("The file " + imageFileName + " can NOT be a absolute path.", getToken());
    }
    if (basePath == null) {
      throw new RuntimeException("The image base path IS NOT defined.");
    }
    filePath = basePath.resolve(imageFileName);
    if (!Files.exists(filePath)) {
      throw new SICCompileTimeException("The file " + filePath + " do NOT exist.", getToken());
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
