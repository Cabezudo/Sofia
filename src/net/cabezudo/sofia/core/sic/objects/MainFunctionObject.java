package net.cabezudo.sofia.core.sic.objects;

import java.util.ArrayList;
import java.util.List;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.server.images.SofiaImage;
import net.cabezudo.sofia.core.sic.elements.SICFunction;
import net.cabezudo.sofia.core.sic.elements.SICParameter;
import net.cabezudo.sofia.core.sic.elements.SICParameterOrFunction;
import net.cabezudo.sofia.core.sic.elements.SICParameters;
import net.cabezudo.sofia.core.sic.exceptions.SICCompilerException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class MainFunctionObject extends SICObjectFunction {

  private final List<SICObject> list;

  public MainFunctionObject(SICParameters parameters) throws SICCompilerException {
    this.list = new ArrayList<>();
    SICParameterOrFunction parameterOrFunction = parameters.consume();
    while (parameterOrFunction != null) {
      if (parameterOrFunction.isParameter()) {
        SICParameter parameter = (SICParameter) parameterOrFunction;
        throw new SICCompilerException("Unexpected function parameter " + parameter.getName() + ".", parameter.getPosition());
      }
      SICFunction functionParameter = (SICFunction) parameterOrFunction;
      SICObject sicObject = functionParameter.compile();
      list.add(sicObject);
      parameterOrFunction = parameters.consume();
    }
  }

  @Override
  public SofiaImage run(SofiaImage ignoredImage) throws SICRuntimeException {
    if (ignoredImage != null) {
      Logger.warning("[MainFunctionObject:run] Invalid image on parameter: %s", ignoredImage.getImagePath());
    }
    SofiaImage sofiaImage = null;
    for (SICObject sicObject : list) {
      sofiaImage = sicObject.run(sofiaImage);
    }
    return sofiaImage;
  }

}