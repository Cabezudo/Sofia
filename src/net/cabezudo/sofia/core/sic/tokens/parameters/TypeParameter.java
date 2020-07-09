package net.cabezudo.sofia.core.sic.tokens.parameters;

import java.nio.file.Path;
import net.cabezudo.sofia.core.sic.elements.SICParameter;
import net.cabezudo.sofia.core.sic.objects.SICObject;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class TypeParameter extends SICParameter {

  public TypeParameter(Token name, Token value) {
    super(name, value);
  }

  @Override
  public boolean isTypeParameter() {
    return true;
  }

  @Override
  public SICObject compile(Path basePath) {
    throw new RuntimeException("Nothing to compile here.");
  }

}
