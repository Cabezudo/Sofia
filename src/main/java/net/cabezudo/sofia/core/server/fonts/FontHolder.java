package net.cabezudo.sofia.core.server.fonts;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import net.cabezudo.sofia.core.configuration.Configuration;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.13
 */
public class FontHolder extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    String requestURI = request.getRequestURI();

    int fontNameStartIndex = requestURI.lastIndexOf("/");
    String fontName = requestURI.substring(fontNameStartIndex + 1);

    Path fontsPath = Configuration.getInstance().getCommonsFontsPath();
    Path fontPath = fontsPath.resolve(fontName);
    int i = fontName.lastIndexOf('.');
    if (i == -1) {
      throw new ServletException("A font must have an extension in order to locate the MIME type.");
    }
    String fontType = fontName.substring(i + 1);

    switch (fontType) {
      case "eot":
        response.setContentType("application/vnd.ms-fontobject");
        break;
      case "otf":
        response.setContentType("application/x-font-opentype");
        break;
      case "sfnt":
        response.setContentType("application/font-sfnt");
        break;
      case "svg":
        response.setContentType("image/svg+xml");
        break;
      case "ttf":
        response.setContentType("application/x-font-ttf");
        break;
      case "woff":
        response.setContentType("application/x-font-woff");
        break;
      case "woff2":
        response.setContentType("application/x-font-woff2");
        break;
      default:
        throw new ServletException("Can't find the MIME " + fontType + " for file: " + fontName);
    }

    try (FileInputStream in = new FileInputStream(fontPath.toFile());
            OutputStream out = response.getOutputStream();) {
      byte[] buffer = new byte[1024];
      int count;
      while ((count = in.read(buffer)) >= 0) {
        out.write(buffer, 0, count);
      }
      out.flush();
    }
  }
}
