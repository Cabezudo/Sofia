package net.cabezudo.sofia.core.system;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.02.21
 */
public class SystemMonitor {

  private SystemMonitor() {
    // Nothing to do here. Utility classes should not have public constructors.
  }

  public static void log(Throwable cause) {
    // TODO registrar esto en algún lado para mostrar
    cause.printStackTrace(System.out);
  }
}
