package net.cabezudo.sofia.core.configuration;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.21
 */
public class Environment {

  public static final String LOCAL = "local";
  public static final String PRODUCTION = "production";

  private static Environment INSTANCE;
  private final String name;

  private Environment() {
    String environmentName = Configuration.getInstance().getEnvironmentName();
    switch (environmentName) {
      case "local":
      case "production":
        this.name = environmentName;
        break;
      default:
        throw new ConfigurationException("Invalid enviroment name: " + environmentName);
    }
  }

  public static Environment getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new Environment();
    }
    return INSTANCE;
  }

  public boolean isLocal() {
    return LOCAL.equals(name);
  }

  public boolean isProduction() {
    return PRODUCTION.equals(name);
  }

}
