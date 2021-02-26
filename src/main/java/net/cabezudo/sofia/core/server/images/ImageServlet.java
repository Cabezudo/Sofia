package net.cabezudo.sofia.core.server.images;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.SofiaImage;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.http.SessionManager;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.logger.Logger;
import net.cabezudo.sofia.sic.SofiaImageCode;
import net.cabezudo.sofia.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.sic.elements.SICUnexpectedEndOfCodeException;
import net.cabezudo.sofia.sic.objects.SICObject;
import net.cabezudo.sofia.sic.objects.SICRuntimeException;
import org.apache.commons.codec.binary.Hex;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.12
 */
public class ImageServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    long startTime = new Date().getTime();

    String imagePathName = request.getRequestURI();

    String imagePartialPathName = imagePathName;
    if (imagePartialPathName.startsWith("/")) {
      imagePartialPathName = imagePartialPathName.substring(1);
    }

    Site site = new SessionManager(request).getSite();
    String queryString = request.getQueryString();
    if (queryString != null) {
      queryString = URLDecoder.decode(queryString, Configuration.getInstance().getEncoding());
    }

    Path basePath = site.getVersionedSourcesPath();
    Path imagePath = basePath.resolve(imagePartialPathName);
    if (!Files.exists(imagePath)) {
      basePath = Configuration.getInstance().getSystemDataPath();
      Logger.debug("File %s DO NOT EXISTS.", imagePath);
      imagePath = basePath.resolve(imagePartialPathName);
      Logger.debug("Trying with %s.", imagePath);
    }

    imagePath = getImagePath(basePath, imagePartialPathName, queryString);
    Logger.debug("Image full path: %s.", imagePath);

    if (!Files.exists(imagePath) || Environment.getInstance().isProduction()) {
      try {
        SofiaImage sofiaImage = createFile(basePath, imagePath, imagePartialPathName, queryString, response);
        BufferedImage image = sofiaImage.getImage();
        ImageIO.write(image, "png", response.getOutputStream());

      } catch (IOException ioe) {
        throw new ServletException(ioe);
      } catch (SICCompileTimeException | SICRuntimeException e) {
        String text = e.getMessage() + " " + e.getPosition();
        returnErrorImage(text, response);
      }
    } else {
      Logger.info("File %s allready exists.", imagePath);
      try (FileInputStream in = new FileInputStream(imagePath.toFile()); OutputStream out = response.getOutputStream();) {
        byte[] buffer = new byte[1024];
        int count = in.read(buffer);
        while (count >= 0) {
          out.write(buffer, 0, count);
          count = in.read(buffer);
        }
        out.flush();
      } catch (FileNotFoundException e) {
        try {
          Logger.severe("File not found: %s.", imagePath);
          response.sendError(404);
        } catch (IOException ioe) {
          throw new ServletException(ioe);
        }
      } catch (IOException e) {
        throw new ServletException(e);
      }
    }
    long endTime = new Date().getTime();
    long time = endTime - startTime;
    Logger.debug("Time: %sms", time);
  }

  private SofiaImage createFile(Path basePath, Path imagePath, String imagePartialPathName, String queryString, HttpServletResponse response) throws ServletException, IOException, SICCompileTimeException, SICRuntimeException {
    ImageManager imageManager = ImageManager.getInstance();
    String code;
    String commonStartCode = "main(loadImage(name=" + imagePartialPathName + ")";
    String commonEndCode = ")";

    if (queryString == null) {
      code = "main(loadImage(name=" + imagePartialPathName + "))";
    } else {
      code = commonStartCode + "," + queryString + commonEndCode;
    }
    SofiaImageCode sofiaImageCode;
    sofiaImageCode = new SofiaImageCode(basePath, code);

    try {
      sofiaImageCode.parse();
    } catch (SICUnexpectedEndOfCodeException e) {
      returnErrorImage(e.getMessage(), response);
    }

    SICObject sicObject;
    sicObject = sofiaImageCode.compile();

    SofiaImage image;
    image = sicObject.run();

    imageManager.save(imagePath, image);
    return image;
  }

  private void returnErrorImage(String text, HttpServletResponse response) throws ServletException {
    BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    Graphics graphics = image.getGraphics();
    Font font = new Font("Verdana", Font.BOLD, 12);
    FontMetrics metrics = graphics.getFontMetrics(font);
    int heigth = metrics.getHeight();
    int width = metrics.stringWidth(text);
    Dimension dimension = new Dimension(width + 2, heigth + 2);
    image = new BufferedImage((int) dimension.getWidth(), (int) dimension.getHeight(), BufferedImage.TYPE_INT_RGB);

    Graphics2D g2d = image.createGraphics();
    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
    g2d.setColor(Color.BLACK);
    g2d.drawString(text, 2, metrics.getAscent() + 1);
    g2d.dispose();
    try {
      ImageIO.write(image, "png", response.getOutputStream());
    } catch (IOException ioe) {
      throw new ServletException(ioe);
    }
  }

  private Path getImagePath(Path basePath, String imagePartialPath, String queryString) {
    Path imagePath = basePath.resolve(imagePartialPath);
    Logger.info("Image path: %s", imagePath);

    int i = imagePartialPath.lastIndexOf('.');
    String fileType = imagePartialPath.substring(i + 1);
    Path generatedBasePath = imagePath.getParent().resolve("cache");

    String unencodedCacheId = generatedBasePath + imagePartialPath + queryString;
    String cacheId = getCacheId(unencodedCacheId);
    return generatedBasePath.resolve(cacheId + "." + fileType);
  }

  private String getCacheId(String id) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] messageDigest = md.digest(id.getBytes());
      char[] chars = Hex.encodeHex(messageDigest);
      return String.valueOf(chars);
    } catch (NoSuchAlgorithmException e) {
      throw new SofiaRuntimeException(e);
    }
  }
}
