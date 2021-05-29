package net.cabezudo.sofia.core.qr;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import net.cabezudo.sofia.core.http.SessionManager;
import net.cabezudo.sofia.core.passwords.Hash;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.05
 */
public class QRImageServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Hash hash = new Hash();
    Site site = new SessionManager(request).getSite();

    String scheme = request.getScheme();
    String serverName = request.getServerName();
    int port = request.getServerPort();
    String path = scheme + "://" + serverName + ":" + port + "/images/upload";
    String url = "v1|" + site.getName() + "|Subir imágenes|" + path;

    QrCode qr = QrCode.encodeText(url, QrCode.Ecc.MEDIUM);
    BufferedImage image = qr.toImage(10, 2);
    OutputStream os = response.getOutputStream();
    ImageIO.write(image, "png", os);
  }
}
