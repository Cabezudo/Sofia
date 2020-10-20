package net.cabezudo.sofia.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import javax.naming.NamingException;
import javax.servlet.DispatcherType;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.sofia.core.configuration.Configuration;
import net.cabezudo.sofia.core.configuration.ConfigurationException;
import net.cabezudo.sofia.core.configuration.DataCreationException;
import net.cabezudo.sofia.core.configuration.DataCreator;
import net.cabezudo.sofia.core.configuration.Environment;
import net.cabezudo.sofia.core.configuration.SofiaDatabaseCreator;
import net.cabezudo.sofia.core.creator.LibraryVersionConflictException;
import net.cabezudo.sofia.core.creator.SiteCreationException;
import net.cabezudo.sofia.core.creator.SiteCreator;
import net.cabezudo.sofia.core.database.DatabaseCreators;
import net.cabezudo.sofia.core.exceptions.ServerException;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.http.SofiaErrorHandler;
import net.cabezudo.sofia.core.http.SofiaHTMLDefaultServlet;
import net.cabezudo.sofia.core.qr.QRImageServlet;
import net.cabezudo.sofia.core.server.fonts.FontHolder;
import net.cabezudo.sofia.core.server.html.CompanyPathTransformationFilter;
import net.cabezudo.sofia.core.server.html.HTMLFilter;
import net.cabezudo.sofia.core.server.html.URLTransformationFilter;
import net.cabezudo.sofia.core.server.images.ImageServlet;
import net.cabezudo.sofia.core.sites.Site;
import net.cabezudo.sofia.core.sites.SiteList;
import net.cabezudo.sofia.core.sites.SiteManager;
import net.cabezudo.sofia.core.sites.domainname.DomainName;
import net.cabezudo.sofia.core.sites.domainname.DomainNameList;
import net.cabezudo.sofia.core.users.autentication.LogoutHolder;
import net.cabezudo.sofia.core.users.authorization.HTMLAuthorizationFilter;
import net.cabezudo.sofia.core.ws.WebServicesUniverse;
import net.cabezudo.sofia.core.ws.servlet.WebServicesServlet;
import net.cabezudo.sofia.logger.Level;
import net.cabezudo.sofia.logger.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.16
 */
public class WebServer {

  private static final WebServer instance = new WebServer();

  private final Server server;

  private WebServer() {
    server = new Server(Configuration.getInstance().getServerPort());
  }

  public static void main(String... args)
          throws ServerException, PortAlreadyInUseException, ConfigurationException, SQLException, IOException, JSONParseException, JSONParseException,
          SiteCreationException, LibraryVersionConflictException, DataCreationException, FileNotFoundException, NamingException {

    Logger.info("Check configuration.");
    try {
      Configuration.getInstance().checkAndCreateFile();
    } catch (ConfigurationException e) {
      Logger.severe(e);
      System.exit(1);
    }

    List<String> arguments = Arrays.asList(args);
    Utils.consoleOutLn("Sofia 0.1 (http://sofia.systems)");

    StartOptions startOptions = new StartOptions(arguments);

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

    SofiaDatabaseCreator mainDefaultDataCreator = new SofiaDatabaseCreator();
    DatabaseCreators defaultDataCreators = soh.readModuleData();

    try {
      if (startOptions.hasDropDatabase()) {
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
      mainDefaultDataCreator.riseDatabaseCreatedFlag();
      mainDefaultDataCreator.createDatabaseStructure();
    }
    for (DataCreator defaultDataCreator : defaultDataCreators) {
      if (!defaultDataCreator.databaseExists()) {
        defaultDataCreator.createDatabase();
        defaultDataCreator.riseDatabaseCreatedFlag();
        defaultDataCreator.createDatabaseStructure();
      }
    }

    if (startOptions.hasConfigureAdministrator()) {
      try {
        soh.createAdministrator();
        System.exit(0);
      } catch (SQLException e) {
        Logger.severe(e);
        System.exit(1);
      }
    }

    if (startOptions.hasChangeUserPassword()) {
      try {
        soh.changeUserPassword();
        System.exit(0);
      } catch (SQLException e) {
        Logger.severe(e);
      }
    }

    if (mainDefaultDataCreator.isDatabaseCreated()) {
      String baseDomainName = soh.getDefaultDomainName();
      Logger.info("Create the default sites.");
      SiteManager.getInstance().create("Manager", "manager", "localhost", baseDomainName);
      SiteManager.getInstance().create("Playground", "playground");
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
      System.out.println("The database has dropped and recreated. Execution terminated.");
      System.exit(0);
    }

    Configuration.getInstance().loadAPIConfiguration(defaultDataCreators);

    Logger.info("Starting server...");
    int port = Configuration.getInstance().getServerPort();
    try {
      ServerSocket ss = new ServerSocket(port);
      ss.close();
    } catch (IOException e) {
      Logger.severe("Can't open the port " + port + ". " + e.getMessage());
      System.exit(1);
    }

    if (false) {
      Site site = SiteManager.getInstance().getByHostame("cdmx.menu");
      SiteCreator.getInstance().createPages(site, "/company/index.html");
    } else {
      WebServer.getInstance().start();
    }
  }

  public static WebServer getInstance() throws ServerException, PortAlreadyInUseException, ConfigurationException {
    return instance;
  }

  private Handler setServer(Site site) throws ServerException, ConfigurationException {
    APIConfiguration apiConfiguration = Configuration.getInstance().getAPIConfiguration();
    try {
      WebServicesUniverse.getInstance().add(apiConfiguration);
    } catch (ClassNotFoundException | PropertyNotExistException e) {
      throw new ConfigurationException("Configuration error. " + e.getMessage(), e);
    }

    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setDisplayName(Integer.toString(site.getId()));
    context.setContextPath("/");
    String sitePath = site.getVersionPath().toString();
    Logger.debug("Create handler for host %s using site path %s.", site.getBaseDomainName().getName(), sitePath);

    context.setResourceBase(sitePath);
    DomainNameList domainNames;
    try {
      domainNames = site.getDomainNames();
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
      virtualHosts[i + 1] = "local." + domainName.getName();
      i += 2;
    }

    context.setVirtualHosts(virtualHosts);

    context.addFilter(URLTransformationFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
    context.addFilter(CompanyPathTransformationFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
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

    ServletHolder defaultServlet = new ServletHolder("static", SofiaHTMLDefaultServlet.class
    );
    context.addServlet(defaultServlet, "/*");

    context.setErrorHandler(new SofiaErrorHandler());

    for (String vh : context.getVirtualHosts()) {
      Logger.debug("Virtual host: " + vh);
    }

    // Check and create mandatory directories and files
    Path cacheImagesPath = site.getSourcesImagesPath().resolve("cache");
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

    return context;
  }

  public static void add(DomainName domainName) {
    HandlerCollection handlerCollection = (HandlerCollection) instance.server.getHandler();

    Handler[] contexts = handlerCollection.getHandlers();

    for (Handler handler : contexts) {
      ServletContextHandler context = (ServletContextHandler) handler;
      String siteName = Integer.toString(domainName.getSiteId());
      if (siteName.equals(context.getDisplayName())) {
        context.addVirtualHosts(new String[]{domainName.getName(), "local." + domainName.getName()});
      }
    }
  }

  public static void delete(DomainName domainName) {
    HandlerCollection handlerCollection = (HandlerCollection) instance.server.getHandler();

    Handler[] contexts = handlerCollection.getHandlers();

    for (Handler handler : contexts) {
      ServletContextHandler context = (ServletContextHandler) handler;
      String siteName = Integer.toString(domainName.getSiteId());
      if (siteName.equals(context.getDisplayName())) {
        context.removeVirtualHosts(new String[]{domainName.getName(), "local." + domainName.getName()});
      }
    }
  }

  private void createCommonsFile(Site site) throws IOException {
    File file = site.getVersionedSourcesCommonsFilePath().toFile();
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

  public void stop() throws ServerException {
    try {
      server.stop();
    } catch (Exception e) {
      throw new ServerException(e);
    }
    Logger.info("Server stoped.");
  }

  private void start() throws ServerException, IOException, ConfigurationException {
    HandlerCollection handlerCollection = new HandlerCollection();

    SiteList siteList;
    try {
      siteList = SiteManager.getInstance().list();
    } catch (SQLException e) {
      throw new ServerException("Can't start server. " + e.getMessage(), e);
    }

    for (Site site : siteList) {
      handlerCollection.addHandler(instance.setServer(site));
      handlerCollection.addHandler(instance.setAPI(site));
    }

    instance.server.setHandler(handlerCollection);
    try {
      server.start();
      server.join();
      Logger.info("Server started.");
    } catch (Exception e) {
      if (Environment.getInstance().isDevelopment()) {
        e.printStackTrace();
      } else {
        Logger.severe(e);
      }
    }
  }
}
