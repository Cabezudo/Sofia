package net.cabezudo.sofia.core.http;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.02.06
 */
public class SofiaErrorHandler {
//public class SofiaErrorHandler extends ErrorHandler {

  protected void writeErrorPage(HttpServletRequest request, Writer writer, int code, String message, boolean showStacks) throws IOException {
    switch (code) {
      // TODO load either, beatifull pages form the user site or beatifull default pages
      case 404:
        writer.write("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\"/>");
        writer.write("<title>Error 404 Not Found</title></head>");
        writer.write("<body><h2>HTTP ERROR: 404</h2><p>" + request.getRequestURI() + " not Found</p><hr /><i><small>Powered by Sofia</small></i>");
        writer.write("</body></html>");
        break;
      case 500:
        writer.write("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\"/>");
        writer.write("<title>Error 500 Internal server error</title></head>");
        writer.write("<body><h2>HTTP ERROR: 500</h2><p>Internal server error</p><hr /><i><small>Powered by Sofia</small></i>");
        writer.write("</body></html>");
        break;
      default:
//        super.writeErrorPage(request, writer, code, message, showStacks);
        break;
    }
  }
}
