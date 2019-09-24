package net.cabezudo.sofia.core.passwords;

import java.util.Base64;
import java.util.UUID;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.15
 */
public class Hash {

  private final String hash;

  public Hash() {
    UUID uuid = UUID.randomUUID();
    hash = Base64.getEncoder().encodeToString(uuid.toString().getBytes());
  }

  public Hash(String hash) {
    this.hash = hash;
  }

  @Override
  public String toString() {
    return hash;
  }
}
