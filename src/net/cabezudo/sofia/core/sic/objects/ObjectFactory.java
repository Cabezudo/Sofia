package net.cabezudo.sofia.core.sic.objects;

import net.cabezudo.sofia.core.sic.elements.SICParameters;
import net.cabezudo.sofia.core.sic.objects.ResizeFunctionObject;
import net.cabezudo.sofia.core.sic.exceptions.SICCompilerException;
import net.cabezudo.sofia.core.sic.objects.LoadImageFunctionObject;
import net.cabezudo.sofia.core.sic.objects.MainFunctionObject;
import net.cabezudo.sofia.core.sic.objects.SICObjectFunction;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class ObjectFactory {

  public static SICObjectFunction get(String name, SICParameters parameters) throws SICCompilerException {
    switch (name) {
      case "main":
        return new MainFunctionObject(parameters);
      case "loadImage":
        return new LoadImageFunctionObject(parameters);
      case "resize":
        return new ResizeFunctionObject(parameters);
      default:
        throw new RuntimeException("[ObjectFactory:SICObjectFunction] Invalid name for a function object: " + name);
    }
  }

}
