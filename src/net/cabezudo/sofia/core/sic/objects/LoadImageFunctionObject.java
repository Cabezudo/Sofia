package net.cabezudo.sofia.core.sic.objects;

import java.io.IOException;
import java.nio.file.Path;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.server.images.SofiaImage;
import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.elements.SICElement;
import net.cabezudo.sofia.core.sic.elements.SICFunction;
import net.cabezudo.sofia.core.sic.elements.SICParameter;
import net.cabezudo.sofia.core.sic.elements.SICParameters;
import net.cabezudo.sofia.core.sic.objects.values.SICImageFilePath;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class LoadImageFunctionObject extends SICObjectFunction {

  private final SICImageFilePath imageFilePath;

  public LoadImageFunctionObject(Path basePath, SICParameters parameters) throws SICCompileTimeException {
    SICElement parameterOrFunction = parameters.consume();
    if (parameterOrFunction.isFunction()) {
      SICFunction functionParameter = (SICFunction) parameterOrFunction;
      throw new SICCompileTimeException("Unexpected function parameter " + functionParameter.getName() + ". Expect a name parameter.", functionParameter.getToken());
    }
    SICParameter parameter = (SICParameter) parameterOrFunction;
    if (!parameter.isNameParameter()) {
      throw new SICCompileTimeException("Unexpected token " + parameter.getName() + ". Expect a name parameter.", parameter.getToken());
    }
    imageFilePath = new SICImageFilePath(basePath, parameter.getValueToken());

    parameterOrFunction = parameters.consume();
    if (parameterOrFunction != null) {
      throw new SICCompileTimeException("Unexpected parameter " + parameter.getName() + ".", parameter.getToken());
    }
  }

  @Override
  public SofiaImage run(SofiaImage ignoredImage) throws SICRuntimeException {
    if (ignoredImage != null) {
      Logger.warning("[CreateImageFunctionObject:run] Invalid image on parameter: %s", ignoredImage.getImagePath());
    }
    SofiaImage sofiaImage;
    Path filePath = imageFilePath.getValue();
    Logger.debug("[CreateImageFunctionObject:run] Create file path %s.", filePath);
    try {
      sofiaImage = new SofiaImage(filePath);
      return sofiaImage;
    } catch (IOException e) {
      throw new SICRuntimeException("Can't read the file " + filePath + ".", imageFilePath.getToken());
    }
  }

}
