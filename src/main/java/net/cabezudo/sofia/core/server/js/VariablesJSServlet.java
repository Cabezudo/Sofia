package net.cabezudo.sofia.core.server.js;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import net.cabezudo.sofia.core.users.User;
import net.cabezudo.sofia.core.users.UserManager;
import net.cabezudo.sofia.core.users.profiles.Profile;
import net.cabezudo.sofia.core.users.profiles.Profiles;
import net.cabezudo.sofia.core.webusers.WebUserDataManager;
import net.cabezudo.sofia.core.webusers.WebUserDataManager.WebUserData;
import net.cabezudo.sofia.emails.EMailNotExistException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.05.31
 */
public class VariablesJSServlet {

  public String getScript(HttpServletRequest request) throws ServletException, IOException, SQLException, EMailNotExistException {
    WebUserData webUserData = WebUserDataManager.getInstance().get(request);

    User user = webUserData.getUser();

    String requestURI = request.getRequestURI();

    int i = requestURI.lastIndexOf("/");
    String company = null;
    if (i > 0) {
      company = requestURI.substring(1, i);
    }

    String lastPage = (String) request.getSession().getAttribute("lastPage");
    String goBackPage = (String) request.getSession().getAttribute("goBackPage");

    StringBuilder sb = new StringBuilder();

    sb.append("const variables = {\n");
    if (company != null && company.isBlank()) {
      sb.append("  company: null,\n");
    } else {
      sb.append("  company: {\n");
      sb.append("    host: '").append(company).append("',\n");
      sb.append("  },\n");
    }
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
    return sb.toString();
  }
}
