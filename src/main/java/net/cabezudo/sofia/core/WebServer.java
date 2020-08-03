package net.cabezudo.sofia.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import javax.servlet.DispatcherType;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.configuration.ConfigurationException;
import net.cabezudo.sofia.core.configuration.DefaultData;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.exceptions.ServerException;
import net.cabezudo.sofia.core.http.SofiaErrorHandler;
import net.cabezudo.sofia.core.qr.QRImageServlet;
import net.cabezudo.sofia.core.server.fonts.FontHolder;
import net.cabezudo.sofia.core.server.html.HTMLFilter;
import net.cabezudo.sofia.core.server.images.ImageServlet;
import net.cabezudo.sofia.core.server.js.JSServlet;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteList;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.users.UserNotExistException;
import net.cabezudo.sofia.core.users.autentication.LogoutHolder;
import net.cabezudo.sofia.core.users.authorization.HTMLAuthorizationFilter;
import net.cabezudo.sofia.core.ws.WebServicesUniverse;
import net.cabezudo.sofia.core.ws.servlet.WebServicesServlet;
import net.cabezudo.sofia.emails.EMailNotExistException;
import net.cabezudo.sofia.logger.Level;
import net.cabezudo.sofia.logger.Logger;
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

  private static WebServer instance;
  private final Server server;

  public static void main(String... args) {
    runServer(args);
  }

  private static void runServer(String... args) {
    List<String> arguments = Arrays.asList(args);
    System.out.println("Sofia 0.1 (http://sofia.systems)");

    StartOptions startOptions = new StartOptions(arguments);
    if (startOptions.hasHelp() || startOptions.hasInvalidArgument()) {
      if (startOptions.hasInvalidArgument()) {
        System.out.println("Invalid argument: " + startOptions.getInvalidArgument());
      }
      System.out.println(startOptions.getHelp());
      System.exit(0);
    }

    if (startOptions.hasDebug()) {
      Logger.setLevel(Level.DEBUG);
    }

    Logger.info("Starting server...");
    try {
      checkAndCreateConfigurationFile();
    } catch (ConfigurationException e) {
      Logger.severe(e);
      System.exit(1);
    }
    int port = Configuration.getInstance().getServerPort();

    try {
      ServerSocket ss = new ServerSocket(port);
      ss.close();
    } catch (IOException e) {
      Logger.severe("Can't open the port " + port + ". " + e.getMessage());
      System.exit(1);
    }

    try {
      DefaultData.create(startOptions);
      WebServer.getInstance().start();
    } catch (FileNotFoundException | PortAlreadyInUseException | ConfigurationException | ServerException | UserNotExistException | EMailNotExistException e) {
      if (Environment.getInstance().isDevelopment()) {
        e.printStackTrace();
      } else {
        Logger.severe(e);
      }
    }
  }

  private static void checkAndCreateConfigurationFile() throws ConfigurationException {
    System.out.print("Checking configuration file path... ");
    if (Configuration.getConfigurationFilePath() == null) {
      System.out.println("Not found.");
      if (System.console() != null) {
        System.out.print("Create configuration file example? [Y/n]: ");
        String createConfigurationFile = System.console().readLine();
        if (createConfigurationFile.isBlank() || "y".equalsIgnoreCase(createConfigurationFile)) {
          try {
            Path configurationFilePath = Configuration.createFile();
            System.out.println("Configure the file " + configurationFilePath + " and run the server again.");
            System.exit(0);
          } catch (ConfigurationException e) {
            System.out.println(e.getMessage());
            System.exit(1);
          }
        }
      }
      Logger.severe("Configuration file not found.");
      System.exit(1);
    }
    System.out.println("OK");
    Configuration.validateConfiguration();
  }

  public static WebServer getInstance() throws ServerException, PortAlreadyInUseException, ConfigurationException {
    if (instance == null) {
      instance = new WebServer();
    }
    return instance;
  }

  private Handler setServer(Site site) throws ServerException, ConfigurationException {
    Path apiConfigurationFilePath = Configuration.getInstance().getAPIConfigurationFile();
    Logger.debug("Load API configuration file: %s", apiConfigurationFilePath);
    JSONObject apiConfiguration;
    try {
      apiConfiguration = JSON.parse(apiConfigurationFilePath, Configuration.getInstance().getEncoding().name()).toJSONObject();
    } catch (JSONParseException e) {
      throw new ConfigurationException("Error in api configuration file " + apiConfigurationFilePath + ". " + e.getMessage(), e);
    } catch (IOException e) {
      throw new ServerException(e);
    }
    try {
      WebServicesUniverse.getInstance().add(apiConfiguration);
    } catch (ClassNotFoundException | PropertyNotExistException e) {
      throw new ConfigurationException("Configuration error." + e.getMessage(), e);
    }

    Logger.debug("Create handler for host %s", site.getBaseDomainName().getName());
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    String sitePath = site.getVersionPath().toString();
    context.setResourceBase(sitePath);
    String[] virtualHosts;
    try {
      virtualHosts = site.getDomainNames().toStringArray();
    } catch (SQLException e) {
      throw new ServerException(e);
    }
    context.setVirtualHosts(virtualHosts);

    context.addFilter(HTMLFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
    context.addFilter(HTMLAuthorizationFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

    ServletHolder logoutHolder = new ServletHolder("logout", LogoutHolder.class);
    context.addServlet(logoutHolder, "/logout");

    ServletHolder apiHolder = new ServletHolder("webServices", WebServicesServlet.class);
    context.addServlet(apiHolder, "/api/*");

    ServletHolder fontsHolder = new ServletHolder("fonts", FontHolder.class);
    context.addServlet(fontsHolder, "/fonts/*");

    ServletHolder imageHolder = new ServletHolder("image", ImageServlet.class);
    context.addServlet(imageHolder, "/images/*");

    ServletHolder qrImageHolder = new ServletHolder("qrImage", QRImageServlet.class);
    context.addServlet(qrImageHolder, "/images/upload/qr.png");

    ServletHolder jsHolder = new ServletHolder("JS", JSServlet.class);
    context.addServlet(jsHolder, "/js/*");

    ServletHolder defaultServlet = new ServletHolder("static", DefaultServlet.class);
    context.addServlet(defaultServlet, "/*");
    context.setErrorHandler(new SofiaErrorHandler());

    for (String vh : context.getVirtualHosts()) {
      Logger.debug("Virtual host: " + vh);
    }

    // Check and create mandatory directories
    Path cacheImagesPath = site.getSourcesImagesPath().resolve("cache");
    try {
      Files.createDirectories(cacheImagesPath);
    } catch (IOException e) {
      throw new ServerException("Can't create the cache image path: " + cacheImagesPath, e);
    }

    return context;
  }

  private Handler setAPI(Site site) {
    Logger.debug("Create API handler for host %s", site.getBaseDomainName().getName());
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    String sitePath = site.getVersionPath().toString();
    context.setResourceBase(sitePath);
    String[] virtualHosts = new String[1];
    virtualHosts[0] = "api." + site.getBaseDomainName().getName();
    context.setVirtualHosts(virtualHosts);
    ServletHolder apiHolder = new ServletHolder("webServices", WebServicesServlet.class);
    context.addServlet(apiHolder, "/*");

    context.setErrorHandler(new SofiaErrorHandler());

    return context;
  }

  private WebServer() throws ServerException, ConfigurationException, PortAlreadyInUseException {
    server = new Server(Configuration.getInstance().getServerPort());
    HandlerCollection handler = new HandlerCollection();

    SiteList siteList;
    try {
      siteList = SiteManager.getInstance().list();
    } catch (SQLException e) {
      throw new ServerException("Can't start server. " + e.getMessage(), e);
    }

    for (Site site : siteList) {
      handler.addHandler(setServer(site));
      handler.addHandler(setAPI(site));
    }

    server.setHandler(handler);

    try {
      server.start();
      server.join();
    } catch (Exception e) {
      if (e.getCause() instanceof java.net.BindException && "Address already in use".equals(e.getCause().getMessage())) {
        throw new PortAlreadyInUseException("Port " + Configuration.getInstance().getServerPort() + " already in use.");
      }
      throw new ServerException("Can't start server. " + e.getMessage(), e);
    }
  }

  public void start() throws ServerException {
    try {
      server.start();
      server.join();
      Logger.info("Server started.");
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  public void stop() throws ServerException {
    try {
      server.stop();
    } catch (Exception e) {
      throw new ServerException(e);
    }
    Logger.info("Server stoped.");
  }
}
