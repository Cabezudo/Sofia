package net.cabezudo.sofia.core.http;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.servlet.DefaultServlet;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.08.25
 */
public class SofiaDefaultServlet extends DefaultServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    System.out.println("********************");
    System.out.println(request);
    System.out.println(request.getServletPath());
    System.out.println(request.getRequestURI());
    System.out.println(request.getContextPath());
    super.doGet(request, response);
  }
}
