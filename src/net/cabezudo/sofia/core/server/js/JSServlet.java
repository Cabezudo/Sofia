package net.cabezudo.sofia.core.server.js;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.system.SystemMonitor;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.core.users.profiles.Profile;
import net.cabezudo.sofia.core.users.profiles.Profiles;
import net.cabezudo.sofia.emails.EMailNotExistException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.31
 */
public class JSServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    Site site = (Site) request.getAttribute("site");
    User user = (User) request.getAttribute("user");

    String requestURI = request.getRequestURI();

    String fileName = requestURI.substring(1);

    Path jsPath = site.getVersionPath();
    Path jsFilePath = jsPath.resolve(fileName);

    response.setContentType("text/javascript");
    try (FileInputStream in = new FileInputStream(jsFilePath.toFile()); OutputStream out = response.getOutputStream();) {

      StringBuilder sb = new StringBuilder();
      sb.append("const variables = {\n");
      String lastPage = (String) request.getSession().getAttribute("lastPage");
      if (lastPage == null) {
        sb.append("  lastPage: null,\n");
      } else {
        sb.append("  lastPage: '").append(lastPage).append("',\n");
      }
      String comebackPage = (String) request.getSession().getAttribute("comebackPage");
      if (comebackPage == null) {
        sb.append("  comebackPage: null,\n");
      } else {
        sb.append("  comebackPage: '").append(comebackPage).append("',\n");
      }
      request.getSession().setAttribute("", requestURI);
      sb.append("  user: ");
      if (user == null) {
        sb.append("null\n");
      } else {
        sb.append("{\n");
        sb.append("  id: '").append(user.getId()).append("',\n");
        sb.append("  email: '").append(user.getMail().getAddress()).append("',\n");
        sb.append("    profiles: [\n");
        Profiles profiles = UserManager.getInstance().getProfiles(user);
        boolean first = true;
        for (Profile profile : profiles) {
          if (first) {
            first = false;
          } else {
            sb.append(",\n");
          }
          sb.append("      {\n");
          sb.append("        id: ").append(profile.getId()).append(",\n");
          sb.append("        name: '").append(profile.getName()).append("'\n");
          sb.append("      }");
        }
        sb.append("\n    ]\n");
        sb.append("  }\n");
      }
      sb.append("};\n");
      sb.append("\n");
      out.write(sb.toString().getBytes(StandardCharsets.UTF_8));

      byte[] buffer = new byte[1024];
      int count;
      while ((count = in.read(buffer)) >= 0) {
        out.write(buffer, 0, count);
      }
      out.flush();
    } catch (SQLException | EMailNotExistException e) {
      SystemMonitor.log(e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
}
