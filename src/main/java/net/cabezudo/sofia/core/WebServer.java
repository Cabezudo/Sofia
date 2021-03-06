package net.cabezudo.sofia.core;

import jakarta.servlet.DispatcherType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Queue;
import javax.naming.NamingException;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.configuration.ConfigurationException;
import net.cabezudo.sofia.core.configuration.DataCreationException;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.configuration.SofiaDatabaseCreator;
import net.cabezudo.sofia.core.creator.LibraryVersionConflictException;
import net.cabezudo.sofia.core.creator.SiteCreationException;
import net.cabezudo.sofia.core.database.oo.SofiaDatabase;
import net.cabezudo.sofia.core.database.sql.DatabaseCreators;
import net.cabezudo.sofia.core.exceptions.DataConversionException;
import net.cabezudo.sofia.core.exceptions.ServerException;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.http.SofiaErrorHandler;
import net.cabezudo.sofia.core.http.SofiaHTMLDefaultServlet;
import net.cabezudo.sofia.core.languages.ChangeLanguageServlet;
import net.cabezudo.sofia.core.qr.QRImageServlet;
import net.cabezudo.sofia.core.server.files.DownloadFileServlet;
import net.cabezudo.sofia.core.server.fonts.FontHolder;
import net.cabezudo.sofia.core.server.html.DataFilter;
import net.cabezudo.sofia.core.server.html.HTMLFilter;
import net.cabezudo.sofia.core.server.html.URLTransformationFilter;
import net.cabezudo.sofia.core.server.images.ImageServlet;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteList;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.sites.domainname.DomainName;
import net.cabezudo.sofia.core.sites.domainname.DomainNameList;
import net.cabezudo.sofia.core.sites.texts.TextManager;
import net.cabezudo.sofia.core.users.autentication.LogoutHolder;
import net.cabezudo.sofia.core.users.authorization.HTMLAuthorizationFilter;
import net.cabezudo.sofia.core.ws.WebServicesUniverse;
import net.cabezudo.sofia.core.ws.servlet.WebServicesServlet;
import net.cabezudo.sofia.logger.Level;
import net.cabezudo.sofia.logger.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.16
 */
public class WebServer {

  private static WebServer INSTANCE;
  private Server server;
  private HandlerCollection handlerCollection;

  public static void main(String... args)
          throws ServerException, PortAlreadyInUseException, ConfigurationException, IOException, JSONParseException, JSONParseException,
          SiteCreationException, LibraryVersionConflictException, DataCreationException, NamingException, ClusterException, PropertyNotExistException, DataConversionException {

    Utils.consoleOutLn("Sofia 0.1 (http://sofia.systems)");
    WebServer webServer = WebServer.getInstance();

    try {
      webServer.configure(args);
    } catch (ConfigurationException | ClusterException e) {
      Logger.severe(e);
      System.exit(1);
    } catch (InvalidParameterException e) {
      Utils.consoleOutLn(e.getMessage());
      Utils.consoleOutLn(StartOptions.getHelp());
      System.exit(0);
    }

    webServer.start();
  }

  public static WebServer getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new WebServer();
    }
    return INSTANCE;
  }

  private WebServer() {
    // Protect the instance
  }

  private void configure(String[] args)
          throws IOException, ConfigurationException, DataCreationException, ClusterException, DataConversionException, JSONParseException,
          PropertyNotExistException, InvalidParameterException {

    Queue<String> arguments = new LinkedList<>(Arrays.asList(args));

    StartOptions startOptions = new StartOptions(arguments);

    if (startOptions.hasHelp()) {
      Utils.consoleOutLn(StartOptions.getHelp());
      System.exit(0);
    }

    if (startOptions.hasDebug()) {
      Logger.setLevel(Level.DEBUG);
    }

    StartOptionsHelper soh = new StartOptionsHelper(startOptions);
    Path customConfigurationFilePath = startOptions.getCustomConfigurationFilePath();
    Configuration.load(customConfigurationFilePath);

    SofiaDatabaseCreator mainDefaultDataCreator = new SofiaDatabaseCreator();
    checkDatabase(startOptions, mainDefaultDataCreator);

    DatabaseCreators defaultDataCreators = soh.readModuleData();

    soh.checkForDropDatabase(mainDefaultDataCreator, defaultDataCreators);
    soh.createDatabases(mainDefaultDataCreator, defaultDataCreators);

    if (startOptions.hasConfigureAdministrator()) {
      soh.createAdministrator();
      System.exit(0);
    }

    if (startOptions.hasChangeUserPassword()) {
      soh.changeUserPassword();
      System.exit(0);
    }

    soh.createDefaultData(mainDefaultDataCreator, defaultDataCreators);

    soh.createTestData(mainDefaultDataCreator, defaultDataCreators);

    if (startOptions.hasDropDatabase()) {
      Utils.consoleOutLn("The database has dropped and recreated. Execution terminated.");
      System.exit(0);
    }

    Logger.info("Starting server...");
    // TODO DatabaseManager MUST loads the default data creators
    Configuration.getInstance().loadAPIConfiguration(defaultDataCreators);
    TextManager.getInstance().loadTexts();
    SofiaDatabase.getInstance().loadData();

    int port = Configuration.getInstance().getServerPort();
    try {
      ServerSocket ss = new ServerSocket(port);
      ss.close();
    } catch (IOException e) {
      Logger.severe("Can't open the port " + port + ". " + e.getMessage());
      System.exit(1);
    }

    // Create and configure a ThreadPool.
    QueuedThreadPool threadPool = new QueuedThreadPool();
    threadPool.setName("main");
    server = new Server(threadPool);
    ServerConnector connector = new ServerConnector(server);
    connector.setPort(Configuration.getInstance().getServerPort());
    server.addConnector(connector);
  }

  private Handler setServer(Site site) throws ServerException, ConfigurationException {
    APIConfiguration apiConfiguration = Configuration.getInstance().getAPIConfiguration();
    try {
      WebServicesUniverse.getInstance().add(apiConfiguration);
    } catch (ClassNotFoundException | PropertyNotExistException e) {
      throw new ConfigurationException("Configuration error. " + e.getMessage(), e);
    }

    ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    handler.setDisplayName(Integer.toString(site.getId()));
    handler.setContextPath("/");
    String sitePath = site.getVersionedBasePath().toString();
    Logger.debug("Create handler for host %s using site path %s.", site.getBaseDomainName().getName(), sitePath);

    handler.setResourceBase(sitePath);
    DomainNameList domainNames;
    try {
      domainNames = site.getDomainNamesList();
    } catch (SQLException e) {
      throw new ServerException(e);
    }
    if (domainNames.isEmpty()) {
      throw new SofiaRuntimeException("No domains names for " + site.getName());
    }

    String[] virtualHosts = new String[domainNames.size() * 2];
    int i = 0;
    for (DomainName domainName : domainNames) {
      virtualHosts[i] = domainName.getName();
      Logger.debug("    %s.", domainName.getName());
      i += 1;
      if (Environment.getInstance().isDevelopment()) {
        virtualHosts[i] = "local." + domainName.getName();
        i += 1;
      }
    }

    handler.setVirtualHosts(virtualHosts);

    handler.addFilter(URLTransformationFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
    handler.addFilter(DataFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
    handler.addFilter(HTMLFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
    handler.addFilter(HTMLAuthorizationFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
    ServletHolder logoutHolder = new ServletHolder("logout", LogoutHolder.class);
    handler.addServlet(logoutHolder, "/logout");

    // TODO Add JSON 500 error to the api
    ServletHolder apiHolder = new ServletHolder("webServices", WebServicesServlet.class);
    handler.addServlet(apiHolder, "/api/*");

    ServletHolder fontsHolder = new ServletHolder("fonts", FontHolder.class);
    handler.addServlet(fontsHolder, "/fonts/*");

    ServletHolder imageHolder = new ServletHolder("image", ImageServlet.class);
    handler.addServlet(imageHolder, "/images/*");

    ServletHolder fileHolder = new ServletHolder("download", DownloadFileServlet.class);
    handler.addServlet(fileHolder, "/download/*");

    ServletHolder changeLanguageHolder = new ServletHolder("changeLanguage", ChangeLanguageServlet.class);
    handler.addServlet(changeLanguageHolder, "/changeLanguage");

    ServletHolder qrImageHolder = new ServletHolder("qrImage", QRImageServlet.class);
    handler.addServlet(qrImageHolder, "/images/upload/qr.png");

    ServletHolder defaultServlet = new ServletHolder("static", SofiaHTMLDefaultServlet.class);
    handler.addServlet(defaultServlet, "/*");

    handler.setErrorHandler(new SofiaErrorHandler());

    for (String vh : handler.getVirtualHosts()) {
      Logger.debug("Virtual host: " + vh);
    }

    // Check and create mandatory directories and files
    Path cacheImagesPath = site.getVersionedSourcesImagesPath().resolve("cache");
    try {
      Files.createDirectories(cacheImagesPath);
    } catch (IOException e) {
      throw new ServerException("Can't create the cache image path: " + cacheImagesPath, e);
    }
    try {
      createCommonsFile(site);
    } catch (IOException e) {
      throw new ServerException("Can't create the commons file for " + site.getName(), e);
    }

    return handler;
  }

  public static void addHostNameToVirtualHost(DomainName domainName) {
    Logger.debug("Add hostname to handler: " + domainName);

    ServletContextHandler[] contexts = (ServletContextHandler[]) INSTANCE.handlerCollection.getHandlers();

    for (ServletContextHandler context : contexts) {
      String siteName = Integer.toString(domainName.getSiteId());
      if (siteName.equals(context.getDisplayName())) {
        Logger.debug("Add virtual host: " + domainName.getName());
        context.addVirtualHosts(new String[]{domainName.getName(), "local." + domainName.getName()});
      }
    }
  }

  public static void deleteHostNameToVirtualHost(DomainName domainName) {
    ServletContextHandler[] contexts = (ServletContextHandler[]) INSTANCE.handlerCollection.getHandlers();

    for (ServletContextHandler context : contexts) {
      String siteName = Integer.toString(domainName.getSiteId());
      if (siteName.equals(context.getClassPath())) {
        context.removeVirtualHosts(new String[]{domainName.getName(), "local." + domainName.getName()});
      }
    }
  }

  public static void addHandler(Site site) throws ClusterException, ServerException, ConfigurationException {
    try {
      Logger.debug("Add handler for domain name: " + site.getName());
      Handler serverHandler = INSTANCE.setServer(site);
      INSTANCE.handlerCollection.addHandler(serverHandler);
      serverHandler.start();
      Handler apiHandler = INSTANCE.setAPI(site);
      INSTANCE.handlerCollection.addHandler(apiHandler);
      apiHandler.start();
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  public static void delHandler(Site site) {
    String siteId = Integer.toString(site.getId());
    for (Handler h : INSTANCE.handlerCollection.getHandlers()) {
      ServletContextHandler sch = (ServletContextHandler) h;
      if (siteId.equals(sch.getDisplayName())) {
        INSTANCE.handlerCollection.removeHandler(h);
      }
    }
  }

  private void createCommonsFile(Site site) throws IOException {
    File file = site.getVersionedSourcesCommonsConfigurationFilePath().toFile();
    Logger.debug("Check for %s.", file.getAbsoluteFile());
    if (file.createNewFile()) {
      try (FileWriter writer = new FileWriter(file)) {
        Logger.debug("%s file created.", file.getName());
        writer.write("{\n");
        writer.write("  \"site\": {\n");
        writer.write("    \"name\": \"" + site.getName() + "\",\n");
        writer.write("    \"shortName\": \"" + site.getName() + "\",\n");
        writer.write("    \"socialMedia\": {\n");
        writer.write("      \"twitter\": {\n");
        writer.write("        \"url\": \"\"\n");
        writer.write("      }\n");
        writer.write("    }\n");
        writer.write("  },\n");
        writer.write("  \"themeName\": \"basic\"\n");
        writer.write("}\n");
      }
    } else {
      Logger.debug("%s file already exists.", file.getName());
    }
  }

  private Handler setAPI(Site site) {
    Logger.debug("Create API handler for host %s", site.getBaseDomainName().getName());
    ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    handler.setDisplayName(Integer.toString(site.getId()));
    handler.setContextPath("/");
    String sitePath = site.getVersionedBasePath().toString();
    handler.setResourceBase(sitePath);
    String[] virtualHosts = new String[1];
    virtualHosts[0] = "api." + site.getBaseDomainName().getName();
    handler.setVirtualHosts(virtualHosts);
    ServletHolder apiHolder = new ServletHolder("webServices", WebServicesServlet.class);
    handler.addServlet(apiHolder, "/*");

    handler.setErrorHandler(new SofiaErrorHandler());

    return handler;
  }

  public void stop() throws ServerException {
    try {
      server.stop();
    } catch (Exception e) {
      throw new ServerException(e);
    }
    Logger.info("Server stoped.");
  }

  public void start() throws ServerException, IOException, ConfigurationException {
    handlerCollection = new HandlerCollection();

    SiteList siteList;
    try {
      siteList = SiteManager.getInstance().list();
    } catch (ClusterException e) {
      throw new ServerException("Can't start server. " + e.getMessage(), e);
    }

    for (Site site : siteList) {
      handlerCollection.addHandler(INSTANCE.setServer(site));
      handlerCollection.addHandler(INSTANCE.setAPI(site));
    }

    INSTANCE.server.setHandler(handlerCollection);

    try {
      server.start();
      Logger.info("Server started");
      server.join();
    } catch (Exception e) {
      if (Environment.getInstance().isDevelopment()) {
        e.printStackTrace();
      } else {
        Logger.severe(e);
      }
    }
  }

  private void checkDatabase(StartOptions startOptions, SofiaDatabaseCreator mainDefaultDataCreator) throws ConfigurationException, DataCreationException {
    String databaseName = Configuration.getInstance().getDatabaseName();
    Utils.consoleOutLn("Using database: " + databaseName);

    if (mainDefaultDataCreator.databaseExists()) {
      return;
    }
    Utils.consoleOutLn("Configured database DO NOT exist: " + databaseName);
    if (!startOptions.hasIDE()) {
      int maxAttempsNumber = 100;
      int i = 0;
      for (; i < maxAttempsNumber; i++) {
        Utils.consoleOut("Create the database or terminate the excecution? [Y/n]: ");
        if (System.console() == null) {
          throw new ConfigurationException("Database do not exist: " + databaseName);
        }
        String response = System.console().readLine();
        if (response != null) {
          if ("n".equalsIgnoreCase(response)) {
            System.exit(1);
          }
          if (response.isBlank() || "y".equalsIgnoreCase(response)) {
            break;
          }
        }
        Utils.consoleOutLn("Invalid response: " + response);
      }
      if (i >= maxAttempsNumber) {
        System.exit(1);
      }
    }
  }
}
