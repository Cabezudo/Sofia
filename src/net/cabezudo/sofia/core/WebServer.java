package net.cabezudo.sofia.core;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import javax.servlet.DispatcherType;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.configuration.ConfigurationFileNotFoundException;
import net.cabezudo.sofia.core.configuration.DefaultData;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.http.SofiaErrorHandler;
import net.cabezudo.sofia.core.logger.Level;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.passwords.PasswordMaxSizeException;
import net.cabezudo.sofia.core.qr.QRImageServlet;
import net.cabezudo.sofia.core.server.fonts.FontHolder;
import net.cabezudo.sofia.core.server.html.HTMLFilter;
import net.cabezudo.sofia.core.server.js.JSServlet;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteList;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.users.autentication.LogoutHolder;
import net.cabezudo.sofia.core.users.authorization.HTMLAuthorizationFilter;
import net.cabezudo.sofia.core.ws.servlet.WebServices;
import net.cabezudo.sofia.emails.EMailMaxSizeException;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.16
 */
public class WebServer {

  private static WebServer INSTANCE;
  private final Server server;

  public static void main(String... args) throws SQLException, EMailMaxSizeException, PasswordMaxSizeException, IOException, InterruptedException {
    List arguments = Arrays.asList(args);
    System.out.println("Sofia 0.1 (http://sofia.systems)");

    StartOptions startOptions = new StartOptions(arguments);
    if (startOptions.hasHelp() || startOptions.hasInvalidArgument()) {
      if (startOptions.hasInvalidArgument()) {
        System.out.println("Invalid argument: " + startOptions.getInvalidArgument());
      }
      System.out.println(startOptions.getHelp());
      System.exit(0);
    }

    if (startOptions.hasIDE() || startOptions.hasDebug()) {
      Logger.setLevel(Level.FINEST);
    } else {
      Logger.setLevel(Level.INFO);
    }

    Logger.info("Starting server...");
    try {
      Configuration.searchFile();
    } catch (ConfigurationFileNotFoundException e) {
      Logger.severe(e);
      System.exit(1);
    }
    try {
      DefaultData.create(startOptions);
      WebServer.getInstance().start();
    } catch (PortAlreadyInUseException e) {
      Logger.severe(e);
      System.exit(1);
    } catch (Exception e) {
      if (Environment.getInstance().isLocal()) {
        e.printStackTrace();
      } else {
        Logger.severe(e);
      }
    }
  }

  public static WebServer getInstance() throws Exception {
    if (INSTANCE == null) {
      INSTANCE = new WebServer();
    }
    return INSTANCE;
  }

  private Handler setServer(Site site) throws SQLException {
    Logger.debug("Create handler for host %s", site.getBaseHost().getName());
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    String sitePath = site.getVersionPath().toString();
    context.setResourceBase(sitePath);
    String[] virtualHosts = site.getHosts().toStringArray();
    context.setVirtualHosts(virtualHosts);

    context.addFilter(HTMLFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
    context.addFilter(HTMLAuthorizationFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

    ServletHolder logoutHolder = new ServletHolder("logout", LogoutHolder.class);
    context.addServlet(logoutHolder, "/logout");

    ServletHolder apiHolder = new ServletHolder("webServices", WebServices.class);
    context.addServlet(apiHolder, "/api/*");

    ServletHolder fontsHolder = new ServletHolder("fonts", FontHolder.class);
    context.addServlet(fontsHolder, "/fonts/*");

    ServletHolder qrImageHolder = new ServletHolder("qrImage", QRImageServlet.class);
    context.addServlet(qrImageHolder, "/images/upload/qr.png");

    ServletHolder jsHolder = new ServletHolder("JS", JSServlet.class);
    context.addServlet(jsHolder, "/js/*");

////    ServletHolder logoutServlet = new ServletHolder("logout", LogoutServlet.class);
////    context.addServlet(logoutServlet, "/logout");
    ServletHolder defaultServlet = new ServletHolder("static", DefaultServlet.class);
    context.addServlet(defaultServlet, "/*");
    context.setErrorHandler(new SofiaErrorHandler());

    for (String vh : context.getVirtualHosts()) {
      Logger.debug("Virtual host: " + vh);
    }

    return context;
  }

  private Handler setAPI(Site site) throws SQLException {
    Logger.debug("Create API handler for host %s", site.getBaseHost().getName());
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    String sitePath = site.getVersionPath().toString();
    context.setResourceBase(sitePath);
    String[] virtualHosts = new String[1];
    virtualHosts[0] = "api." + site.getBaseHost().getName();
    context.setVirtualHosts(virtualHosts);
//    HashSessionIdManager idManager = new HashSessionIdManager();
//    server.setSessionIdManager(idManager);
//    HashSessionManager manager = new HashSessionManager();
//    SessionHandler sessions = new SessionHandler(manager);
//    sessions.setHandler(context);
    ServletHolder apiHolder = new ServletHolder("webServices", WebServices.class);
    context.addServlet(apiHolder, "/*");

    context.setErrorHandler(new SofiaErrorHandler());

    return context;
  }

  private WebServer() throws Exception {

//    QueuedThreadPool threadPool = new QueuedThreadPool();
//    threadPool.setMaxThreads(100);
////    // Setup server
//    server = new Server(threadPool);
//    server.manage(threadPool);
//
    server = new Server(Configuration.getInstance().getServerPort());
    HandlerCollection handler = new HandlerCollection();

    SiteList siteList = SiteManager.getInstance().getAll();

    for (Site site : siteList) {
      handler.addHandler(setServer(site));
      handler.addHandler(setAPI(site));
    }

    server.setHandler(handler);

    try {
      server.start();
    } catch (java.io.IOException e) {
      if (e.getCause() instanceof java.net.BindException && "Address already in use".equals(e.getCause().getMessage())) {
        throw new PortAlreadyInUseException("Port " + Configuration.getInstance().getServerPort() + " already in use.");
      }
    }
    server.join();

//    QueuedThreadPool threadPool = new QueuedThreadPool();
//    threadPool.setMaxThreads(100);
//    // Setup server
//    server = new Server(threadPool);
//    server.manage(threadPool);
//    HandlerList handlers = new HandlerList();
//    ResourceHandler resourceHandler = new ResourceHandler();
//
//    ServletContextHandler context = new ServletContextHandler();
//    context.setContextPath("/");
    Site site = SiteManager.getInstance().getById(1);
    String sitePath = site.getVersionPath().toString();
//    context.setResourceBase(sitePath);
//    handlers.setHandlers(new Handler[]{resourceHandler, new DefaultHandler()});
//    server.setHandler(handlers);
//    server.start();
//    server.join();

//    server = new Server(8080);
//
//    // Create the ResourceHandler. It is the object that will actually handle the request for a given file. It is
//    // a Jetty Handler object so it is suitable for chaining with other handlers as you will see in other examples.
//    ResourceHandler resource_handler = new ResourceHandler();
//
//    // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
//    // In this example it is the current directory but it can be configured to anything that the jvm has access to.
//    resource_handler.setDirectoriesListed(true);
//    resource_handler.setWelcomeFiles(new String[]{"index.html"});
//    System.out.println(sitePath);
//    resource_handler.setResourceBase(sitePath);
//
//    // Add the ResourceHandler to the server.
//    HandlerList handlers = new HandlerList();
//    handlers.setHandlers(new Handler[]{resource_handler, new DefaultHandler()});
//    server.setHandler(handlers);
//
//    // Start things up! By using the server.join() the server thread will join with the current thread.
//    // See "http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.html#join()" for more details.
//    server.start();
//    server.join();
  }

  public void start() throws Exception {
    server.start();
    server.join();
    Logger.info("Server started.");
  }

  public void stop() throws Exception {
    server.stop();
    Logger.info("Server stoped.");
  }
}
