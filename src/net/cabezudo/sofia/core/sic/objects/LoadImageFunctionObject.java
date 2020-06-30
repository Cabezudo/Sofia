package net.cabezudo.sofia.core.sic.objects;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.server.images.SofiaImage;
import net.cabezudo.sofia.core.sic.SICCompilerMessages;
import net.cabezudo.sofia.core.sic.elements.SICElement;
import net.cabezudo.sofia.core.sic.elements.SICFunction;
import net.cabezudo.sofia.core.sic.elements.SICParameter;
import net.cabezudo.sofia.core.sic.elements.SICParameters;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class LoadImageFunctionObject extends SICObjectFunction {

  private final Token nameTokenValue;

  public LoadImageFunctionObject(SICParameters parameters, SICCompilerMessages messages) {
    SICElement parameterOrFunction = parameters.consume();
    if (parameterOrFunction.isFunction()) {
      SICFunction functionParameter = (SICFunction) parameterOrFunction;
      messages.add("Unexpected function parameter " + functionParameter.getName() + ". Expect a name parameter.", functionParameter.getPosition());
    }
    SICParameter parameter = (SICParameter) parameterOrFunction;
    if (!parameter.isNameParameter()) {
      messages.add("Unexpected function parameter " + parameter.getName() + ". Expect a name parameter.", parameter.getPosition());
    }
    nameTokenValue = parameter.getValueToken();

    parameterOrFunction = parameters.consume();
    if (parameterOrFunction != null) {
      messages.add("Unexpected parameter " + parameter.getName() + ".", parameter.getPosition());
    }
  }

  @Override
  public SofiaImage run(SofiaImage ignoredImage) throws SICRuntimeException {
    if (ignoredImage != null) {
      Logger.warning("[CreateImageFunctionObject:run] Invalid image on parameter: %s", ignoredImage.getImagePath());
    }
    SofiaImage sofiaImage;
    String fileName = nameTokenValue.getValue();
    Path filePath = Paths.get(fileName);
    Logger.debug("[CreateImageFunctionObject:run] Create file path %s.", filePath);
    try {
      sofiaImage = new SofiaImage(filePath);
      return sofiaImage;
    } catch (IOException e) {
      throw new SICRuntimeException("Can't read the file " + filePath + ".", nameTokenValue.getPosition());
    }
  }

}
