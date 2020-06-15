package net.cabezudo.sofia.core.server.images;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import net.cabezudo.sofia.core.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.12
 */
public class SofiaImage {

  private final Path imagePath;
  private final BufferedImage image;
  private int width;
  private int height;

  public SofiaImage(Path imagePath, BufferedImage image) {
    this.imagePath = imagePath;
    this.image = image;
    this.width = image.getWidth();
    this.height = image.getHeight();
    Logger.debug("[SofiaImage:constructor(Path, BufferedImage)] Create image %s with width %s and height %s.", imagePath, width, height);
  }

  public SofiaImage(SofiaImage sofiaImage) {
    this(sofiaImage.getImagePath(), sofiaImage.getImage());
  }

  public SofiaImage(Path imagePath) throws IOException {
    this.imagePath = imagePath;
    image = ImageIO.read(imagePath.toFile());
    this.width = image.getWidth();
    this.height = image.getHeight();
    Logger.debug("[SofiaImage:constructor(Path)] Create image %s with width %s and height %s.", imagePath, width, height);
  }

  public BufferedImage getImage() {
    return image;
  }

  public Path getImagePath() {
    return imagePath;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
