package net.cabezudo.sofia.core.files;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.http.url.parser.tokens.URLTokens;
import net.cabezudo.sofia.core.ws.responses.Response;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.13
 */
public class UploadImageFileService extends UploadFileService {

  public UploadImageFileService(HttpServletRequest request, HttpServletResponse response, URLTokens tokens) throws ServletException, IOException {
    super(request, response, tokens);
  }

  @Override
  protected Path getTargetPath() {
    return site.getSourcesImagesPath();
  }

  @Override
  protected Response getOKResponse() {
    JSONObject jsonData = new JSONObject();
    jsonData.add(new JSONPair("filePath", getFilePartialPath().toString()));
    return new Response(Response.Status.OK, Response.Type.CREATE, jsonData, "image.upload.ok", getFilePartialPath().toString());
  }
}
