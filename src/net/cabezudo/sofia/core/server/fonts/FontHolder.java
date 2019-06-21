package net.cabezudo.sofia.core.server.fonts;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    do {
      if (fontName.endsWith("woff")) {
        response.setContentType("application/x-font-woff");
        break;
      }
      if (fontName.endsWith("woff2")) {
        response.setContentType("application/x-font-woff2");
        break;
      }
      throw new ServletException("Can't find the MIME for file: " + fontName);
    } while (false);

    try (FileInputStream in = new FileInputStream(fontPath.toFile()); OutputStream out = response.getOutputStream();) {
      byte[] buffer = new byte[1024];
      int count;
      while ((count = in.read(buffer)) >= 0) {
        out.write(buffer, 0, count);
      }
      out.flush();
    }
  }
}
