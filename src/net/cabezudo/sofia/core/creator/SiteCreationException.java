package net.cabezudo.sofia.core.creator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.06.06
 */
public class SiteCreationException extends Exception {

  private static final long serialVersionUID = 1L;

  SiteCreationException(String message) {
    super(message);
  }

  SiteCreationException(Throwable e) {
    super(e);
  }
}
