package net.cabezudo.sofia.core.sic.elements;

import java.nio.file.Path;
import net.cabezudo.sofia.core.sic.objects.SICObject;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public abstract class SICElement {

  private final Token token;

  public SICElement(Token token) {
    this.token = token;
  }

  public Token getToken() {
    return token;
  }

  public abstract String toString(int i);

  public abstract SICObject compile(Path basePath) throws SICCompileTimeException;

  public boolean isFunction() {
    return false;
  }

  public boolean isParameter() {
    return false;
  }
}
