package net.cabezudo.sofia.core.server.images;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import net.cabezudo.sofia.SofiaImage;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.sic.SofiaImageCode;

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

  Path save(Path imagePath, SofiaImage sofiaImage) {
    if (Files.exists(imagePath)) {
      if (Environment.getInstance().isProduction()) {
        Logger.debug("[SofiaImage:save] The file %s already exist. Don't save.", imagePath);
      } else {
        Logger.debug("[SofiaImage:save] The file %s already exist but IS NOT production. Don't save.", imagePath);
      }
      return imagePath;
    } else {
      Logger.debug("[SofiaImage:save] The file %s DO NOT exist. Save.", imagePath);
    }

    String imagePathName = imagePath.toString();
    String formatName = imagePathName.substring(imagePathName.lastIndexOf(".") + 1);
    try {
      ImageIO.write(sofiaImage.getImage(), formatName, imagePath.toFile());
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return imagePath;
  }

  SofiaImage runCode(SofiaImage sofiaImage) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  SofiaImage run(SofiaImage sofiaImage, SofiaImageCode sofiaImageCode) {
    return sofiaImage;
  }

}
