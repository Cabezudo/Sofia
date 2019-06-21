package net.cabezudo.sofia.core.passwords;

import java.util.UUID;
import javax.xml.bind.DatatypeConverter;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.15
 */
public class Hash {

  private final String hash;

  public Hash() {
    UUID uuid = UUID.randomUUID();
    hash = DatatypeConverter.printBase64Binary(uuid.toString().getBytes());
  }

  public Hash(String hash) {
    this.hash = hash;
  }

  @Override
  public String toString() {
    return hash;
  }
}
