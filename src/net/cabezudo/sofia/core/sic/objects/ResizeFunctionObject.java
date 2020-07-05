package net.cabezudo.sofia.core.sic.objects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.server.images.SofiaImage;
import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.elements.SICElement;
import net.cabezudo.sofia.core.sic.elements.SICFunction;
import net.cabezudo.sofia.core.sic.elements.SICParameter;
import net.cabezudo.sofia.core.sic.elements.SICParameters;
import net.cabezudo.sofia.core.sic.objects.values.SICAspect;
import net.cabezudo.sofia.core.sic.objects.values.SICHeight;
import net.cabezudo.sofia.core.sic.objects.values.SICScale;
import net.cabezudo.sofia.core.sic.objects.values.SICWidth;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class ResizeFunctionObject extends SICObjectFunction {

  private final List<SICObject> list;
  private SICWidth widthParameter;
  private SICHeight heightParameter;
  private SICScale scaleParameter;
  private SICAspect aspectParameter;

  public ResizeFunctionObject(Path basePath, Token token, SICParameters parameters) throws SICCompileTimeException {
    this.list = new ArrayList<>();

    SICElement parameterOrFunction = parameters.consume();
    while (parameterOrFunction != null) {
      if (parameterOrFunction.isParameter()) {
        SICParameter parameter = (SICParameter) parameterOrFunction;
        SICParameter parameterNameToken = parameter;
        String parameterName = parameter.getName();
        Token parameterValue = parameter.getValueToken();
        switch (parameterName) {
          case "width":
            if (scaleParameter != null) {
              throw new SICCompileTimeException("You can't use width parameter along height and scale.", parameterNameToken.getToken());
            }
            if (heightParameter != null && aspectParameter != null) {
              throw new SICCompileTimeException("You can't use width parameter along height and aspect.", parameterNameToken.getToken());
            }
            widthParameter = new SICWidth(parameterValue);
            Logger.debug("Set the width parameter to %s.", widthParameter);
            break;
          case "height":
            if (scaleParameter != null) {
              throw new SICCompileTimeException("You can't use height parameter along width and scale.", parameterNameToken.getToken());
            }
            if (widthParameter != null && aspectParameter != null) {
              throw new SICCompileTimeException("You can't use height parameter along width and aspect.", parameterNameToken.getToken());
            }
            heightParameter = new SICHeight(parameterValue);
            Logger.debug("Set the height parameter to %s.", heightParameter);
            break;
          case "scale":
            if (widthParameter != null) {
              throw new SICCompileTimeException("You can't use scale parameter along width.", parameterNameToken.getToken());
            }
            if (heightParameter != null) {
              throw new SICCompileTimeException("You can't use scale parameter along height.", parameterNameToken.getToken());
            }
            if (aspectParameter != null) {
              throw new SICCompileTimeException("You can't use scale parameter along aspect.", parameterNameToken.getToken());
            }
            scaleParameter = new SICScale(parameterValue);
            Logger.debug("Set the height parameter to %s.", heightParameter);
            break;
          case "aspect":
            if (scaleParameter != null) {
              throw new SICCompileTimeException("You can't use aspect parameter along scale.", parameterNameToken.getToken());
            }
            if (widthParameter != null && heightParameter != null) {
              throw new SICCompileTimeException("You can't use aspect parameter along width and height.", parameterNameToken.getToken());
            }
            aspectParameter = new SICAspect(parameterValue);
            Logger.debug("Set the height parameter to %s.", heightParameter);
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
    Logger.debug("Width setted to %s", widthParameter);
    Logger.debug("Height setted to %s", heightParameter);
    Logger.debug("Scale setted to %s", scaleParameter);
    Logger.debug("Factor setted to %s", aspectParameter);
    // Check if we have all we need to calculate
    if (widthParameter == null && heightParameter == null && aspectParameter == null && scaleParameter == null) {
      throw new SICCompileTimeException("Can't calculate the size with this parameters.", token);
    }
    if (widthParameter == null && heightParameter == null && aspectParameter != null) {
      throw new SICCompileTimeException("Factor parameter on resize function need a parameter width or height in order to calculate the image size.", token);
    }
  }

  @Override
  public SofiaImage run(SofiaImage sofiaImage) throws SICRuntimeException {
    Integer width = null;
    Integer height = null;
    do {
      if (scaleParameter != null) {
        double scaleValue = scaleParameter.getValue().doubleValue();
        if (scaleParameter.isPercentage()) {
          width = (int) (sofiaImage.getWidth() * scaleValue / 100);
          height = (int) (sofiaImage.getHeight() * scaleValue / 100);
        } else {
          width = (int) (sofiaImage.getWidth() * scaleValue);
          height = (int) (sofiaImage.getHeight() * scaleValue);
        }
        break;
      }
      if (widthParameter != null && heightParameter != null) {
        if (widthParameter.isPercentage()) {
          width = sofiaImage.getWidth() * widthParameter.getValue().intValue() / 100;
        } else {
          width = widthParameter.getValue().intValue();
        }
        if (heightParameter.isPercentage()) {
          height = sofiaImage.getHeight() * heightParameter.getValue().intValue() / 100;
        } else {
          height = heightParameter.getValue().intValue();
        }
        break;
      }
      if (widthParameter != null) {
        double ratio = sofiaImage.getWidth() / (double) sofiaImage.getHeight();
        if (aspectParameter != null) {
          ratio = aspectParameter.getValue().doubleValue();
          Logger.debug("[ResizeFunctionObject:run] Resize image using width %s and aspect %s", widthParameter, aspectParameter);
        }
        width = widthParameter.getValue().intValue();
        height = (int) (width / ratio);
        break;
      }
      if (heightParameter != null) {
        double ratio = sofiaImage.getWidth() / (double) sofiaImage.getHeight();
        if (aspectParameter != null) {
          ratio = aspectParameter.getValue().doubleValue();
          Logger.debug("[ResizeFunctionObject:run] Resize image using height %s and aspect %s", heightParameter, aspectParameter);
        }
        height = heightParameter.getValue().intValue();
        width = (int) (height * ratio);
      }
    } while (false);

    Logger.debug("[ResizeFunctionObject:run] Resize image to width of %s and a height of %s", width, height);
    BufferedImage newImage = new BufferedImage(width, height, sofiaImage.getImage().getType());
    Graphics2D g2d = newImage.createGraphics();

    g2d.drawImage(sofiaImage.getImage(), 0, 0, width, height, null);
    g2d.dispose();

    SofiaImage newSofiaImage = new SofiaImage(sofiaImage.getImagePath(), newImage);
    return newSofiaImage;

  }

}
