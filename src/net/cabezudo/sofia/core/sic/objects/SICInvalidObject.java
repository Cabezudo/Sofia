package net.cabezudo.sofia.core.sic.objects;

import net.cabezudo.sofia.core.server.images.SofiaImage;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class SICInvalidObject extends SICObject {

  @Override
  public SofiaImage run(SofiaImage sofiaImage) throws SICRuntimeException {
    throw new RuntimeException("You must not run an invalid object!");
  }
}
