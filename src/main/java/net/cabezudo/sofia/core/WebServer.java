package net.cabezudo.sofia.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.naming.NamingException;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.sofia.core.cluster.ClusterException;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.configuration.ConfigurationException;
import net.cabezudo.sofia.core.configuration.DataCreationException;
import net.cabezudo.sofia.core.configuration.DataCreator;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.configuration.SofiaDatabaseCreator;
import net.cabezudo.sofia.core.creator.LibraryVersionConflictException;
import net.cabezudo.sofia.core.creator.SiteCreationException;
import net.cabezudo.sofia.core.database.oo.SofiaDatabase;
import net.cabezudo.sofia.core.database.sql.DatabaseCreators;
import net.cabezudo.sofia.core.exceptions.DataConversionException;
import net.cabezudo.sofia.core.exceptions.ServerException;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.sites.domainname.DomainName;
import net.cabezudo.sofia.core.sites.texts.TextManager;
import net.cabezudo.sofia.logger.Level;
import net.cabezudo.sofia.logger.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.16
 */
public class WebServer {

  private static WebServer INSTANCE;
  private Server server;

  public static void main(String... args)
          throws ServerException, PortAlreadyInUseException, ConfigurationException, IOException, JSONParseException, JSONParseException,
          SiteCreationException, LibraryVersionConflictException, DataCreationException, NamingException, ClusterException, PropertyNotExistException, DataConversionException {

//    Queue<String> arguments = new LinkedList<>(Arrays.asList(args));
//    StartOptions startOptions = new StartOptions(arguments);
    WebServer webServer = WebServer.getInstance();
//    webServer.configure(startOptions);
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

  private void configure(StartOptions startOptions)
          throws IOException, ConfigurationException, DataCreationException, ClusterException, DataConversionException, JSONParseException, PropertyNotExistException {

    Utils.consoleOutLn("Sofia 0.1 (http://sofia.systems)");

    if (startOptions.hasHelp() || startOptions.hasInvalidArgument()) {
      if (startOptions.hasInvalidArgument()) {
        Utils.consoleOutLn("Invalid argument: " + startOptions.getInvalidArgument());
      }
      Utils.consoleOutLn(startOptions.getHelp());
      System.exit(0);
    }

    if (startOptions.hasDebug()) {
      Logger.setLevel(Level.DEBUG);
    }

    StartOptionsHelper soh = new StartOptionsHelper(startOptions);

    Logger.info("Check configuration.");
    try {
      String customConfigurationFile = startOptions.getCustomConfigurationFile();
      Path customConfigurationFilePath = null;
      if (customConfigurationFile != null) {
        customConfigurationFilePath = Paths.get(customConfigurationFile);
        if (!Files.exists(customConfigurationFilePath)) {
          throw new InvalidParameterException("Configuration file not exist: " + customConfigurationFile);
        }
      }
      Configuration.load(customConfigurationFilePath);
    } catch (ConfigurationException e) {
      Logger.severe(e);
      System.exit(1);
    }

    SofiaDatabaseCreator mainDefaultDataCreator = new SofiaDatabaseCreator();
    String databaseName = Configuration.getInstance().getDatabaseName();
    Utils.consoleOutLn("Using database: " + databaseName);
    if (!mainDefaultDataCreator.databaseExists()) {
      Utils.consoleOutLn("Configured database DO NOT exist: " + databaseName);
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
    DatabaseCreators defaultDataCreators = soh.readModuleData();

    try {
      if (startOptions.hasDropDatabase()) {

        Path path = Configuration.getInstance().getClusterFileLogPath();
        Files.deleteIfExists(path);

        for (DataCreator defaultDataCreator : defaultDataCreators) {
          defaultDataCreator.dropDatabase();
        }
        mainDefaultDataCreator.dropDatabase();
      }
    } catch (DataCreationException e) {
      if (Environment.getInstance().isDevelopment()) {
        e.printStackTrace();
      } else {
        Logger.severe(e);
      }
    }

    if (!mainDefaultDataCreator.databaseExists()) {
      mainDefaultDataCreator.createDatabase();
      mainDefaultDataCreator.createDatabaseStructure();
      mainDefaultDataCreator.riseDatabaseCreatedFlag();
    }
    for (DataCreator defaultDataCreator : defaultDataCreators) {
      if (!defaultDataCreator.databaseExists()) {
        defaultDataCreator.createDatabase();
        defaultDataCreator.createDatabaseStructure();
        defaultDataCreator.riseDatabaseCreatedFlag();
      }
    }

    if (startOptions.hasConfigureAdministrator()) {
      try {
        soh.createAdministrator();
        System.exit(0);
      } catch (ClusterException e) {
        Logger.severe(e);
        System.exit(1);
      }
    }

    if (startOptions.hasChangeUserPassword()) {
      try {
        soh.changeUserPassword();
        System.exit(0);
      } catch (ClusterException e) {
        Logger.severe(e);
      }
    }

    if (mainDefaultDataCreator.isDatabaseCreated()) {
      String baseDomainName = soh.getDefaultDomainName();
      Logger.info("Create the default sites.");
      SiteManager.getInstance().create("Manager", Paths.get("manager"), "manager", "localhost", baseDomainName);
      SiteManager.getInstance().create("Playground", Paths.get("playground"), "playground");
      soh.createAdministrator();
      mainDefaultDataCreator.createDefaultData();
    }

    for (DataCreator defaultDataCreator : defaultDataCreators) {
      if (defaultDataCreator.isDatabaseCreated()) {
        defaultDataCreator.createDefaultData();
      }
    }

    if (startOptions.hasCreateTestData()) {
      if (mainDefaultDataCreator.isDatabaseCreated()) {
        Logger.info("Create test data for sofia.");
        mainDefaultDataCreator.createTestData();
      }
      for (DataCreator defaultDataCreator : defaultDataCreators) {
        if (defaultDataCreator.isDatabaseCreated()) {
          defaultDataCreator.createTestData();
        }
      }
    }

    if (startOptions.hasDropDatabase()) {
      Utils.consoleOutLn("The database has dropped and recreated. Execution terminated.");
      System.exit(0);
    }

    Logger.info("Starting server...");

    // TODO DatabaseManager MUST loads the default data creators
    Configuration.getInstance().loadAPIConfiguration(defaultDataCreators);
    // TODO Text manager MUST load the texts
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
//    server = new Server(Configuration.getInstance().getServerPort());
    server = new Server();

    ServerConnector connector = new ServerConnector(server);
    connector.setPort(Configuration.getInstance().getServerPort());

    HttpConfiguration https = new HttpConfiguration();
    https.addCustomizer(new SecureRequestCustomizer());

    SslContextFactory.Server ssl = new SslContextFactory.Server();
    ssl.setCrlPath("/home/esteban/Documents/hayquecomer.com/goddady/hayquecomer.com.certificate/6371e601f67d0a67.crt");
//    ssl.setKeyStorePath(Configuration.getInstance().getKeyStoreFileName().toString());
//    ssl.setKeyStorePassword("password");
//    ssl.setKeyManagerPassword("password");

    ServerConnector sslConnector = new ServerConnector(server,
            new SslConnectionFactory(ssl, "http/1.1"),
            new HttpConnectionFactory(https));
    sslConnector.setPort(9998);

    server.setConnectors(new Connector[]{connector, sslConnector});

  }

//  private Handler setServer(Site site) throws ServerException, ConfigurationException {
//    APIConfiguration apiConfiguration = Configuration.getInstance().getAPIConfiguration();
//    try {
//      WebServicesUniverse.getInstance().add(apiConfiguration);
//    } catch (ClassNotFoundException | PropertyNotExistException e) {
//      throw new ConfigurationException("Configuration error. " + e.getMessage(), e);
//    }
//
//    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//    context.setDisplayName(Integer.toString(site.getId()));
//    context.setContextPath("/");
//    String sitePath = site.getVersionPath().toString();
//    Logger.debug("Create handler for host %s using site path %s.", site.getBaseDomainName().getName(), sitePath);
//
//    context.setResourceBase(sitePath);
//    DomainNameList domainNames;
//    try {
//      domainNames = site.getDomainNames();
//    } catch (SQLException e) {
//      throw new ServerException(e);
//    }
//    if (domainNames.isEmpty()) {
//      throw new SofiaRuntimeException("No domains names for " + site.getName());
//    }
//
//    String[] virtualHosts = new String[domainNames.size() * 2];
//    int i = 0;
//    for (DomainName domainName : domainNames) {
//      virtualHosts[i] = domainName.getName();
//      Logger.debug("    %s.", domainName.getName());
//      i += 1;
//      if (Environment.getInstance().isDevelopment()) {
//        virtualHosts[i] = "local." + domainName.getName();
//        i += 1;
//      }
//    }
//
//    context.setVirtualHosts(virtualHosts);
//
//    context.addFilter(URLTransformationFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
//    context.addFilter(DataFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
//    context.addFilter(HTMLFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
//    context.addFilter(HTMLAuthorizationFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
//    ServletHolder logoutHolder = new ServletHolder("logout", LogoutHolder.class);
//    context.addServlet(logoutHolder, "/logout");
//
//    // TODO Add JSON 500 error to the api
//    ServletHolder apiHolder = new ServletHolder("webServices", WebServicesServlet.class);
//    context.addServlet(apiHolder, "/api/*");
//
//    ServletHolder fontsHolder = new ServletHolder("fonts", FontHolder.class);
//    context.addServlet(fontsHolder, "/fonts/*");
//
//    ServletHolder imageHolder = new ServletHolder("image", ImageServlet.class);
//    context.addServlet(imageHolder, "/images/*");
//
//    ServletHolder changeLanguageHolder = new ServletHolder("changeLanguage", ChangeLanguageServlet.class);
//    context.addServlet(changeLanguageHolder, "/changeLanguage");
//
//    ServletHolder qrImageHolder = new ServletHolder("qrImage", QRImageServlet.class);
//    context.addServlet(qrImageHolder, "/images/upload/qr.png");
//
//    ServletHolder defaultServlet = new ServletHolder("static", SofiaHTMLDefaultServlet.class
//    );
//    context.addServlet(defaultServlet, "/*");
//
//    context.setErrorHandler(new SofiaErrorHandler());
//
//    for (String vh : context.getVirtualHosts()) {
//      Logger.debug("Virtual host: " + vh);
//    }
//
//    // Check and create mandatory directories and files
//    Path cacheImagesPath = site.getSourcesImagesPath().resolve("cache");
//    try {
//      Files.createDirectories(cacheImagesPath);
//    } catch (IOException e) {
//      throw new ServerException("Can't create the cache image path: " + cacheImagesPath, e);
//    }
//    try {
//      createCommonsFile(site);
//    } catch (IOException e) {
//      throw new ServerException("Can't create the commons file for " + site.getName(), e);
//    }
//
//    return context;
//  }
  public static void add(DomainName domainName) {
//    HandlerCollection handlerCollection = (HandlerCollection) INSTANCE.server.getHandler();
//
//    Handler[] contexts = handlerCollection.getHandlers();
//
//    for (Handler handler : contexts) {
//      ServletContextHandler context = (ServletContextHandler) handler;
//      String siteName = Integer.toString(domainName.getSiteId());
//      if (siteName.equals(context.getDisplayName())) {
//        context.addVirtualHosts(new String[]{domainName.getName(), "local." + domainName.getName()});
//      }
//    }
  }

  public static void delete(DomainName domainName) {
//    HandlerCollection handlerCollection = (HandlerCollection) INSTANCE.server.getHandler();
//
//    Handler[] contexts = handlerCollection.getHandlers();
//
//    for (Handler handler : contexts) {
//      ServletContextHandler context = (ServletContextHandler) handler;
//      String siteName = Integer.toString(domainName.getSiteId());
//      if (siteName.equals(context.getDisplayName())) {
//        context.removeVirtualHosts(new String[]{domainName.getName(), "local." + domainName.getName()});
//      }
//    }
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

//  private Handler setAPI(Site site) {
//    Logger.debug("Create API handler for host %s", site.getBaseDomainName().getName());
//    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//    context.setContextPath("/");
//    String sitePath = site.getVersionPath().toString();
//    context.setResourceBase(sitePath);
//    String[] virtualHosts = new String[1];
//    virtualHosts[0] = "api." + site.getBaseDomainName().getName();
//    context.setVirtualHosts(virtualHosts);
//    ServletHolder apiHolder = new ServletHolder("webServices", WebServicesServlet.class);
//    context.addServlet(apiHolder, "/*");
//
//    context.setErrorHandler(new SofiaErrorHandler());
//
//    return context;
//  }
  public void stop() throws ServerException {
    try {
      server.stop();
    } catch (Exception e) {
      throw new ServerException(e);
    }
    Logger.info("Server stoped.");
  }

  public void start() throws ServerException, FileNotFoundException {
// Create and configure a ThreadPool.
    QueuedThreadPool threadPool = new QueuedThreadPool();
    threadPool.setName("server");

// Create a Server instance.
    server = new Server(threadPool);

    // The HTTP configuration object.
    HttpConfiguration httpConfig = new HttpConfiguration();
// Add the SecureRequestCustomizer because we are using TLS.
    httpConfig.addCustomizer(new SecureRequestCustomizer(false));

// The ConnectionFactory for HTTP/1.1.
    HttpConnectionFactory http11 = new HttpConnectionFactory(httpConfig);

// Configure the SslContextFactory with the keyStore information.
    SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();

    File file = new File("/home/sofia/ssl/sofia.keystore");
    if (!file.exists()) {
      throw new FileNotFoundException(file.toString());
    }

    sslContextFactory.setKeyStorePath(file.toString());
    sslContextFactory.setKeyStorePassword("esteban");

// The ConnectionFactory for TLS.
    SslConnectionFactory tls = new SslConnectionFactory(sslContextFactory, http11.getProtocol());

    ServerConnector connector = new ServerConnector(server, tls, http11);
    connector.setPort(8443);

// Add the Connector to the Server
    server.addConnector(connector);

// Set a simple Handler to handle requests/responses.
    server.setHandler(new AbstractHandler() {
      @Override
      public void handle(String target, Request jettyRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Mark the request as handled so that it
        // will not be processed by other handlers.
        response.getWriter().print("nada");
        jettyRequest.setHandled(true);
        response.setStatus(200);
        response.setHeader("X-URL", request.getRequestURI());
        response.setHeader("X-HOST", request.getServerName());
      }
    });

    try {
      // Start the Server so it starts accepting connections from clients.
      server.start();
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  public void _start() throws ServerException, IOException, ConfigurationException {
//    HandlerCollection handlerCollection = new HandlerCollection();
//
//    SiteList siteList;
//    try {
//      siteList = SiteManager.getInstance().list();
//    } catch (ClusterException e) {
//      throw new ServerException("Can't start server. " + e.getMessage(), e);
//    }
//
//    for (Site site : siteList) {
//      handlerCollection.addHandler(INSTANCE.setServer(site));
//      handlerCollection.addHandler(INSTANCE.setAPI(site));
//    }
//
//    INSTANCE.server.setHandler(handlerCollection);
//    try {
//      server.start();
//      Logger.info("Server started");
//      server.join();
//    } catch (Exception e) {
//      if (Environment.getInstance().isDevelopment()) {
//        e.printStackTrace();
//      } else {
//        Logger.severe(e);
//      }
//    }
  }
}
