package net.cabezudo.sofia.core.server.images;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.sic.SofiaImageCode;
import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.objects.SICObject;
import net.cabezudo.sofia.core.sic.objects.SICRuntimeException;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.12
 */
public class ImageServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    ImageManager imageManager = ImageManager.getInstance();

    String requestURI = request.getRequestURI();
    int imageNameStartIndex = requestURI.lastIndexOf("/");
    String imageName = requestURI.substring(imageNameStartIndex + 1);

    Site site = (Site) request.getAttribute("site");
    Path imagePath = site.getSourcesImagesPath().resolve(imageName);

    String queryString = request.getQueryString();
    String code;
    String commonStartCode = "main(loadImage(name=" + imagePath + "),";
    String commonEndCode = ")";

    if (queryString == null) {
      code = "main(loadImage(name=" + imagePath + "))";
    } else {
      try {
        code = commonStartCode + URLDecoder.decode(queryString, StandardCharsets.UTF_8.toString()) + commonEndCode;
      } catch (IllegalArgumentException e) {
        throw new ServletException("Can't decode the query string");
      }
    }
    Logger.debug("[ImageServlet:doGet] %s", code);

    SofiaImageCode sofiaImageCode;
    sofiaImageCode = new SofiaImageCode(code);

    SICObject sicObject;
    try {
      sicObject = sofiaImageCode.compile();
    } catch (SICCompileTimeException e) {
      throw new ServletException(e);
    }

    SofiaImage image;
    try {
      image = sicObject.run();
    } catch (SICRuntimeException e) {
      throw new ServletException(e);
    }

    String sofiaImageCodeString = sofiaImageCode.getShortCode();
    String preId = sofiaImageCodeString.substring(commonStartCode.length() - 1);
    String id = preId.substring(0, preId.length() - commonEndCode.length());

    Path outputImagePath = imageManager.save(image, imagePath, id);

    Logger.info("[ImageServlet:doGet] Image path %s.", outputImagePath);
    try (FileInputStream in = new FileInputStream(outputImagePath.toFile()); OutputStream out = response.getOutputStream();) {
      byte[] buffer = new byte[1024];
      int count = in.read(buffer);
      while (count >= 0) {
        out.write(buffer, 0, count);
        count = in.read(buffer);
      }
      out.flush();
    }
  }
}
