package net.cabezudo.sofia.core.cluster;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2021.02.22
 */
public class ClusterException extends Exception {

  public ClusterException(Throwable cause) {
    super(cause);
  }

  public ClusterException(String message) {
    super(message);
  }

  public ClusterException(String message, Throwable cause) {
    super(message, cause);
  }

}
