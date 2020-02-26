package net.cabezudo.sofia.core.ws;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.02.26
 */
class WebServiceMethod {

  private final String url;
  private final String className;
  private final Class<?> clazz;

  WebServiceMethod(String url, String className) throws ClassNotFoundException {
    this.url = url;
    this.className = className;
    this.clazz = Class.forName(className);
  }

  @Override
  public String toString() {
    return "[ url = " + url + ", className = " + className + " ]";
  }
}
