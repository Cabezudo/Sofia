package net.cabezudo.sofia.core.server.files;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import net.cabezudo.sofia.core.http.SessionManager;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.logger.Logger;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.12
 */
public class DownloadFileServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String filePathName = request.getRequestURI();

    String filePartialPathName = filePathName;
    if (filePartialPathName.startsWith("/")) {
      filePartialPathName = filePartialPathName.substring(10);
    } else {
      filePartialPathName = filePartialPathName.substring(9);
    }

    Site site = new SessionManager(request).getSite();

    String inlineParameter = request.getParameter("inline");

    boolean inline = inlineParameter != null && "true".equals(inlineParameter.toLowerCase());

    Path filePath = site.getVersionedSourcesFilesPath().resolve(filePartialPathName);
    Logger.debug("Search file path: %s.", filePath);
    if (!Files.exists(filePath)) {
      Logger.debug("File not found.", filePath);
      filePath = site.getCustomFilesPath().resolve(filePartialPathName);
      Logger.debug("New search file path: %s.", filePath);
    }
    if (!Files.exists(filePath)) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // TODO set the content type (autodetect if user want and explicit in the query string, default: check the database for the file or  binary for download)
    //response.setContentType("text/plain");
    Path fileName = filePath.getFileName();
    // TODO Use the query string to force the attachemnt and name if the user want. Default: Check a value for the file in database or download the file
    if (inline) {
      response.setHeader("Content-disposition", "inline");
    } else {
      response.setHeader("Content-disposition", "attachment; filename=" + fileName);
    }
    try (InputStream is = Files.newInputStream(filePath); OutputStream os = response.getOutputStream()) {

      byte[] buffer = new byte[1024]; // TODO Define this value using the memory

      int numBytesRead;
      while ((numBytesRead = is.read(buffer)) > 0) {
        os.write(buffer, 0, numBytesRead);
      }
    } catch (IOException ioe) {
      throw new ServletException(ioe);
    }
  }
}
