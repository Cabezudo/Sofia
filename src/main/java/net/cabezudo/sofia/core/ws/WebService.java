package net.cabezudo.sofia.core.ws;

import org.eclipse.jetty.http.HttpMethod;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.02.26
 */
class WebService {

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
}
