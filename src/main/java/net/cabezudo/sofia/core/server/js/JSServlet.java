package net.cabezudo.sofia.core.server.js;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.system.SystemMonitor;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.core.users.profiles.Profile;
import net.cabezudo.sofia.core.users.profiles.Profiles;
import net.cabezudo.sofia.core.webusers.WebUserDataManager;
import net.cabezudo.sofia.core.webusers.WebUserDataManager.ClientData;
import net.cabezudo.sofia.emails.EMailNotExistException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.31
 */
public class JSServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    try {
      ClientData clientData = WebUserDataManager.getInstance().get(request);

      Site site = (Site) request.getAttribute("site");
      User user = clientData.getUser();

      String requestURI = request.getRequestURI();
      String fileName = requestURI.substring(1);

      Path jsPath = site.getVersionPath();
      Path jsFilePath = jsPath.resolve(fileName);

      response.setContentType("text/javascript");

      try ( OutputStream out = response.getOutputStream();) {
        if ("js/variables.js".equals(fileName)) {
          String lastPage = (String) request.getSession().getAttribute("lastPage");
          String goBackPage = (String) request.getSession().getAttribute("goBackPage");

          StringBuilder sb = new StringBuilder();

          sb.append("const variables = {\n");
          if (lastPage == null) {
            sb.append("  lastPage: null,\n");
          } else {
            sb.append("  lastPage: '").append(lastPage).append("',\n");
          }
          if (goBackPage == null) {
            sb.append("  goBackPage: null,\n");
          } else {
            sb.append("  goBackPage: '").append(goBackPage).append("',\n");
          }
          String message = (String) request.getSession().getAttribute("message");
          if (message == null) {
            sb.append("  message: null,\n");
          } else {
            sb.append("  message: ").append(message).append(",\n");
            request.getSession().removeAttribute("message");
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
          out.write(sb.toString().getBytes(Configuration.getInstance().getEncoding()));
        } else {
          try ( FileInputStream in = new FileInputStream(jsFilePath.toFile());) {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = in.read(buffer)) >= 0) {
              out.write(buffer, 0, count);
            }
            out.flush();
          }
        }
      }
    } catch (EMailNotExistException | SQLException e) {
      SystemMonitor.log(e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
}
