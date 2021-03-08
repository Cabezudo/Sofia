package net.cabezudo.sofia.core.ws;

import java.util.Objects;
import org.eclipse.jetty.http.HttpMethod;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.02.26
 */
class WebService implements Comparable<WebService> {

  public static final int GET = 1;
  public static final int POST = 2;
  public static final int DELETE = 3;
  public static final int PUT = 4;

  private final HttpMethod method;
  private final String path;
  private final String className;
  private final Class<?> clazz;

  WebService(HttpMethod method, String path, String className) throws ClassNotFoundException {
    this.method = method;
    // TODO Validate that the path use only number, lowercase characters and curly braces. Is important because the order of the paths allow the match with the fixed paths first and the parametrized paths later
    this.path = path;
    this.className = className;
    this.clazz = Class.forName(className);
    // TODO Check for method excecute NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
  }

  @Override
  public String toString() {
    return "[ method = " + method + " path = " + path + ", className = " + className + " ]";
  }

  String getPath() {
    return path;
  }

  String getClassName() {
    return className;
  }

  Class<?> getServiceClass() {
    return clazz;
  }

  HttpMethod getMethod() {
    return method;
  }

  @Override
  public int compareTo(WebService ws) {
    return path.compareTo(ws.path);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof WebService) {
      WebService ws = (WebService) o;
      return path.equals(ws.path);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + Objects.hashCode(this.path);
    return hash;
  }
}
