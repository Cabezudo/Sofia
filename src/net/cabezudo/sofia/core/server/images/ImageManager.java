package net.cabezudo.sofia.core.server.images;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sic.SofiaImageCode;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.12
 */
class ImageManager {

  private static ImageManager INSTANCE;

  static ImageManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ImageManager();
    }
    return INSTANCE;
  }

  Path save(SofiaImage sofiaImage, Path imagePath, String code) throws IOException {
    Path generatedBasePath = imagePath.getParent().resolve("generated");

    Path imageFileNamePath = imagePath.getFileName();
    String imageName = imageFileNamePath.toString();
    String formatName = imageName.substring(imageName.lastIndexOf(".") + 1);

    Path path;
    if (code == null) {
      path = imagePath;
    } else {
      String fileName = imagePath.getFileName().toString();
      Pattern pattern = Pattern.compile(File.separator);
      String scapedCode = code.replaceAll(pattern.toString(), "_");
      path = generatedBasePath.resolve(fileName + "?" + scapedCode);
    }

    if (Files.exists(path) && Environment.getInstance().isProduction()) {
      Logger.debug("[SofiaImage:save] The file %s already exist. Don't save.", path);
      return path;
    } else {
      Logger.debug("[SofiaImage:save] The file %s DO NOT exist. Save.", path);
    }

    ImageIO.write(sofiaImage.getImage(), formatName, path.toFile());

    return path;
  }

  SofiaImage runCode(SofiaImage sofiaImage) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  SofiaImage run(SofiaImage sofiaImage, SofiaImageCode sofiaImageCode) {
    return sofiaImage;
  }

}
