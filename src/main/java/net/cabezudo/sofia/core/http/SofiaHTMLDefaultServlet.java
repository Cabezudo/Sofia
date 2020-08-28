package net.cabezudo.sofia.core.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.sofia.core.http.domains.DomainName;
import net.cabezudo.sofia.core.server.html.SofiaHTMLServletRequest;
import net.cabezudo.sofia.core.server.js.VariablesJSServlet;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.emails.EMailNotExistException;
import net.cabezudo.sofia.logger.Logger;
import org.eclipse.jetty.servlet.DefaultServlet;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.25
 */
public class SofiaHTMLDefaultServlet extends DefaultServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

    SofiaHTMLServletRequest request = new SofiaHTMLServletRequest(req);

    DomainName domainName = new DomainName(request.getServerName());
    String requestURI = request.getRequestURI();

    try {
      Site site = (Site) request.getAttribute("site");
      if (site == null) {
        throw new ServletException("Site for " + domainName + " NOT FOUND.");
      }

      if (requestURI.endsWith("variables.js")) {
        VariablesJSServlet variablesJSServlet = new VariablesJSServlet();
        String script = variablesJSServlet.getScript(request);
        response.setContentType("text/javascript");
        PrintWriter writer = response.getWriter();
        writer.print(script);
        writer.flush();
      } else {
        Logger.debug("Request server name: %s", request.getServerName());
        Logger.debug("Request URI: %s", request.getRequestURI());
        super.doGet(request, response);
      }
    } catch (SQLException | EMailNotExistException e) {
      throw new ServletException(e);
    }
  }
}
