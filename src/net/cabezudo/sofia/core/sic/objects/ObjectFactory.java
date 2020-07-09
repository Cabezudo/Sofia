package net.cabezudo.sofia.core.sic.objects;

import java.nio.file.Path;
import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.elements.SICParameters;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class ObjectFactory {

  public static SICObjectFunction get(Path basePath, Token token, SICParameters parameters) throws SICCompileTimeException {
    switch (token.getValue()) {
      case "main":
        return new MainFunctionObject(basePath, parameters);
      case "loadImage":
        return new LoadImageFunctionObject(basePath, parameters);
      case "resize":
        return new ResizeFunctionObject(basePath, token, parameters);
      case "brightness":
        return new BrightnessFunctionObject(basePath, token, parameters);
      case "gray":
        return new GrayFunctionObject(basePath, token, parameters);
      default:
        throw new RuntimeException("[ObjectFactory:SICObjectFunction] Invalid name for a function object: " + token.getValue());
    }
  }
}
