package net.cabezudo.sofia.core.sic.objects;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.cabezudo.sofia.core.server.images.SofiaImage;
import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.elements.SICElement;
import net.cabezudo.sofia.core.sic.elements.SICFunction;
import net.cabezudo.sofia.core.sic.elements.SICParameter;
import net.cabezudo.sofia.core.sic.elements.SICParameters;
import net.cabezudo.sofia.core.sic.objects.values.SICPercentage;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class BrightnessFunctionObject extends SICObjectFunction {

  private final List<SICObject> list;
  private SICPercentage valueParameter;

  public BrightnessFunctionObject(Path basePath, Token token, SICParameters parameters) throws SICCompileTimeException {
    this.list = new ArrayList<>();

    SICElement parameterOrFunction = parameters.consume();
    while (parameterOrFunction != null) {
      if (parameterOrFunction.isParameter()) {
        SICParameter parameter = (SICParameter) parameterOrFunction;
        SICParameter parameterNameToken = parameter;
        String parameterName = parameter.getName();
        Token parameterValue = parameter.getValueToken();
        switch (parameterName) {
          case "value":
            valueParameter = new SICPercentage(parameterValue);
            break;
          default:
            throw new SICCompileTimeException("Unexpected parameter " + parameterName + ".", parameterNameToken.getToken());
        }
      } else {
        SICFunction functionParameter = (SICFunction) parameterOrFunction;
        SICObject sicObject = functionParameter.compile(basePath);
        list.add(sicObject);
      }
      parameterOrFunction = parameters.consume();
    }
  }

  @Override
  public SofiaImage run(SofiaImage sofiaImage) throws SICRuntimeException {
    SofiaImage newSofiaImage = sofiaImage;
    return newSofiaImage;
  }

}
