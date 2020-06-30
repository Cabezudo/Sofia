package net.cabezudo.sofia.core.sic.objects;

import net.cabezudo.sofia.core.sic.SICCompilerMessages;
import net.cabezudo.sofia.core.sic.elements.SICParameters;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class ObjectFactory {

  public static SICObjectFunction get(String name, SICParameters parameters, SICCompilerMessages messages) {
    switch (name) {
      case "main":
        return new MainFunctionObject(parameters, messages);
      case "loadImage":
        return new LoadImageFunctionObject(parameters, messages);
      case "resize":
        return new ResizeFunctionObject(parameters, messages);
      default:
        throw new RuntimeException("[ObjectFactory:SICObjectFunction] Invalid name for a function object: " + name);
    }
  }
}
