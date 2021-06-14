package net.cabezudo.sofia.core.http;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.sofia.core.cluster.ClusterException;
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

    response.setContentType("text/html; charset=UTF-8");

    try (PrintWriter writer = response.getWriter()) {
      Site site = new SessionManager(request).getSite();
      if (site == null) {
        throw new ServletException("Site for " + domainName + " NOT FOUND.");
      }

      if (requestURI.endsWith("variables.js")) {
        VariablesJSServlet variablesJSServlet = new VariablesJSServlet();
        String script = variablesJSServlet.getScript(request);
        response.setContentType("text/javascript");
        writer.print(script);
        writer.flush();
      } else {
        Logger.debug("Request server name: %s", request.getServerName());
        Logger.debug("Request URI: %s", request.getRequestURI());
        super.doGet(request, response);
      }
    } catch (ClusterException | EMailNotExistException | JSONParseException | URISyntaxException e) {
      throw new ServletException(e);
    }
  }
}
