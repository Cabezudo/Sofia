package net.cabezudo.sofia.core.files;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.ws.responses.Response;
import net.cabezudo.sofia.core.ws.servlet.services.Service;
import org.eclipse.jetty.server.Request;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.13
 */
public abstract class UploadFileService extends Service {

  private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
  private Path filePartialPath;

  public UploadFileService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException, IOException {
    super(request, response, tokens);
  }

  protected Path getFilePartialPath() {
    return filePartialPath;
  }

  protected abstract Path getTargetPath();

  protected abstract Response getOKResponse();

  @Override
  public void post() throws ServletException {
    try {
      Path imagesTargetPath = getTargetPath();

      String contentType = request.getContentType();
      if (contentType != null && contentType.startsWith("multipart/")) {
        request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);

        Part filePart = request.getPart("file");
        String filenamSubmitted = filePart.getSubmittedFileName();
        String filename = Paths.get(filenamSubmitted).getFileName().toString(); // MSIE fix.

        Path newFilePath = imagesTargetPath.resolve(filename);
        filePartialPath = site.getSourcesImagesPath().relativize(newFilePath);

        InputStream is = filePart.getInputStream();
        // TODO Check if we need a a while to read big files. Improve the performance.
        byte[] buffer = new byte[is.available()];
        is.read(buffer);

        File file = newFilePath.toFile();
        // TODO check if the file existe and send a warning
        try (OutputStream outStream = new FileOutputStream(file);) {
          outStream.write(buffer);
        }

        out.print(getOKResponse().toJSON(webUserData.getActualLanguage()));
      } else {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The request MUST be multipart.");
      }
    } catch (IOException e) {
      try {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
      } catch (IOException ioe) {
        throw new ServletException(ioe);
      }
    }
  }
}
